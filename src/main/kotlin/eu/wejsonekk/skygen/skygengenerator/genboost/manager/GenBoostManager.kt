package eu.wejsonekk.skygen.skygengenerator.genboost.manager

import eu.wejsonekk.skygen.skygengenerator.api.event.GenBoostActivateEvent
import eu.wejsonekk.skygen.skygengenerator.api.event.GenBoostEndEvent
import eu.wejsonekk.skygen.skygengenerator.api.event.GenBoostExpireEvent
import eu.wejsonekk.skygen.skygengenerator.api.event.GenBoostLotteryEvent
import eu.wejsonekk.skygen.skygengenerator.api.genboost.GenBoost
import eu.wejsonekk.skygen.skygengenerator.api.genboost.GenBoostType
import eu.wejsonekk.skygen.skygengenerator.api.generator.Generator
import eu.wejsonekk.skygen.skygengenerator.config.impl.GenBoostConfig
import eu.wejsonekk.skygen.skygengenerator.config.impl.GenBoostPresetConfig
import eu.wejsonekk.skygen.skygengenerator.generator.service.GeneratorService
import eu.wejsonekk.skygen.skygengenerator.genboost.bossbar.GenBoostBossBarService
import eu.wejsonekk.skygen.skygengenerator.genboost.model.ActiveGenBoost
import eu.wejsonekk.skygen.skygengenerator.genboost.model.GenBoostImpl
import eu.wejsonekk.skygen.skygengenerator.notice.NoticeService
import eu.wejsonekk.skygen.skygengenerator.util.DurationFormat
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import org.slf4j.Logger
import java.time.Duration
import java.time.Instant
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.ThreadLocalRandom

/**
 * Runtime manager of the gen boost system: owns active boosts, the periodic
 * lottery and the boss bar updates. All methods are expected to run on the
 * main thread.
 */
class GenBoostManager(
    private val plugin: Plugin,
    private val generatorService: GeneratorService,
    private val config: GenBoostConfig,
    private val bossBarService: GenBoostBossBarService,
    private val noticeService: NoticeService,
    private val logger: Logger,
) {

    private val activeBoosts = CopyOnWriteArrayList<ActiveGenBoost>()

    private var task: BukkitTask? = null
    private var nextLotteryAt: Instant = Instant.now()

    // --- lifecycle ---

    fun start() {
        this.stop()
        this.nextLotteryAt = Instant.now().plus(this.safeInterval())
        this.task = Bukkit.getScheduler().runTaskTimer(
            this.plugin,
            Runnable { this.tick() },
            20L,
            20L,
        )
    }

    fun stop() {
        this.task?.cancel()
        this.task = null
        this.activeBoosts.forEach { this.bossBarService.hideBoost(it) }
        this.activeBoosts.clear()
        this.bossBarService.hideCountdown()
    }

    // --- API used by commands / listeners ---

    fun activate(
        type: GenBoostType,
        multiplier: Double,
        duration: Duration,
        ownerId: UUID?,
        name: String,
    ): ActiveGenBoost? {
        if (duration.isNegative || duration.isZero) {
            return null
        }

        val boost = GenBoostImpl(
            type = type,
            multiplier = multiplier.coerceAtLeast(1.0),
            duration = duration,
            ownerId = ownerId,
            name = name,
        )

        return this.activateBoost(boost)
    }

    /**
     * Activates an already built [boost]. Fires a
     * [GenBoostActivateEvent] which may cancel the activation.
     */
    fun activateBoost(boost: GenBoost): ActiveGenBoost? {
        val event = GenBoostActivateEvent(boost)
        Bukkit.getPluginManager().callEvent(event)
        if (event.isCancelled) {
            return null
        }

        val now = Instant.now()
        val active = ActiveGenBoost.of(boost, now)
        this.activeBoosts.add(active)
        this.bossBarService.showBoost(active, this.bossBarViewers(active), now)

        this.logger.info(
            "Activated {} gen boost '{}' (x{}) for {}",
            boost.type,
            boost.name,
            boost.multiplier,
            DurationFormat.format(boost.duration),
        )
        return active
    }

    /**
     * Stops a running boost before its natural expiry. Fires a
     * [GenBoostEndEvent] which may cancel the stop.
     *
     * @return `true` when the boost was stopped
     */
    fun endBoost(id: UUID): Boolean {
        val active = this.activeBoosts.firstOrNull { it.id == id } ?: return false

        val event = GenBoostEndEvent(active.boost)
        Bukkit.getPluginManager().callEvent(event)
        if (event.isCancelled) {
            return false
        }

        return this.remove(active)
    }

    private fun remove(active: ActiveGenBoost): Boolean {
        if (!this.activeBoosts.remove(active)) {
            return false
        }
        this.bossBarService.hideBoost(active)
        return true
    }

    fun activeBoosts(): List<ActiveGenBoost> = this.activeBoosts.toList()

    /**
     * Compound multiplier applied to [generator] right now. Global boosts affect
     * every generator, personal boosts only generators owned by their owner.
     */
    fun multiplierFor(generator: Generator): Double {
        val now = Instant.now()
        var result = 1.0
        for (active in this.activeBoosts) {
            if (active.isExpired(now)) {
                continue
            }
            when (active.type) {
                GenBoostType.GLOBAL -> result *= active.multiplier
                GenBoostType.PERSONAL -> {
                    if (active.ownerId != null && active.ownerId == generator.ownerId) {
                        result *= active.multiplier
                    }
                }
            }
        }
        return result
    }

    fun onPlayerJoin(player: Player) {
        val now = Instant.now()
        this.activeBoosts.forEach { active ->
            if (this.isVisibleTo(active, player)) {
                this.bossBarService.showBoost(active, listOf(player), now)
            }
        }

        if (this.config.lottery.enabled && this.config.lottery.countdownBossBar) {
            val remaining = Duration.between(now, this.nextLotteryAt)
            if (!remaining.isNegative && !remaining.isZero) {
                this.bossBarService.showCountdown(remaining, this.safeInterval(), listOf(player))
            }
        }
    }

    // --- internal tick ---

    private fun tick() {
        val now = Instant.now()

        this.activeBoosts.filter { it.isExpired(now) }.forEach { active ->
            if (this.remove(active)) {
                Bukkit.getPluginManager().callEvent(GenBoostExpireEvent(active.boost))
                this.announceExpired(active)
            }
        }

        this.activeBoosts.forEach { this.bossBarService.updateBoost(it, now) }

        if (this.config.lottery.enabled) {
            this.tickLottery(now)
        }
    }

    private fun tickLottery(now: Instant) {
        val remaining = Duration.between(now, this.nextLotteryAt)
        if (remaining.isNegative || remaining.isZero) {
            this.drawLottery(now)
            return
        }

        if (this.config.lottery.countdownBossBar) {
            this.bossBarService.updateCountdown(remaining, this.safeInterval(), this.chatAudience())
        }
    }

    private fun drawLottery(now: Instant) {
        this.nextLotteryAt = now.plus(this.safeInterval())
        val preset = this.pickWeighted() ?: return

        val boost = GenBoostImpl(
            type = GenBoostType.GLOBAL,
            multiplier = preset.multiplier,
            duration = preset.duration,
            ownerId = null,
            name = preset.name,
        )

        val event = GenBoostLotteryEvent(boost)
        Bukkit.getPluginManager().callEvent(event)
        if (event.isCancelled) {
            return
        }

        this.activateBoost(boost) ?: return

        this.chatAudience().forEach { viewer ->
            this.noticeService.create()
                .viewer(viewer)
                .notice { it.genBoostLotteryDraw }
                .placeholder("{name}", preset.name)
                .placeholder("{multiplier}", preset.multiplier.toString())
                .placeholder("{duration}", DurationFormat.format(preset.duration))
                .send()
        }
    }

    private fun announceExpired(active: ActiveGenBoost) {
        val viewers = when (active.type) {
            GenBoostType.GLOBAL -> this.chatAudience()
            GenBoostType.PERSONAL -> active.ownerId?.let { Bukkit.getPlayer(it) }?.let { listOf(it) } ?: emptyList()
        }

        viewers.forEach { viewer ->
            this.noticeService.create()
                .viewer(viewer)
                .notice { it.genBoostExpired }
                .placeholder("{name}", active.name)
                .send()
        }
    }

    // --- helpers ---

    private fun pickWeighted(): GenBoostPresetConfig? {
        val presets = this.config.lottery.presets.values.filter { it.weight > 0 }
        if (presets.isEmpty()) {
            return null
        }

        var roll = ThreadLocalRandom.current().nextInt(presets.sumOf { it.weight })
        for (preset in presets) {
            roll -= preset.weight
            if (roll < 0) {
                return preset
            }
        }
        return presets.last()
    }

    private fun bossBarViewers(active: ActiveGenBoost): Collection<Player> = when (active.type) {
        GenBoostType.GLOBAL -> Bukkit.getOnlinePlayers().toList()
        GenBoostType.PERSONAL -> active.ownerId?.let { Bukkit.getPlayer(it) }?.let { listOf(it) } ?: emptyList()
    }

    private fun isVisibleTo(active: ActiveGenBoost, player: Player): Boolean = when (active.type) {
        GenBoostType.GLOBAL -> true
        GenBoostType.PERSONAL -> active.ownerId == player.uniqueId
    }

    private fun chatAudience(): Collection<Player> =
        if (this.config.lottery.announceToAll) Bukkit.getOnlinePlayers().toList() else this.nearbyPlayers()

    private fun nearbyPlayers(): Collection<Player> {
        val result = LinkedHashSet<Player>()
        for (generator in this.generatorService.cachedGenerators()) {
            val location = generator.location.toBukkitLocation() ?: continue
            val world = location.world ?: continue
            result.addAll(world.getNearbyPlayers(location, ANNOUNCE_RADIUS))
        }
        return result
    }

    private fun safeInterval(): Duration =
        if (this.config.lottery.interval.isNegative || this.config.lottery.interval.isZero) {
            Duration.ofMinutes(5)
        } else {
            this.config.lottery.interval
        }

    private companion object {
        private const val ANNOUNCE_RADIUS = 32.0
    }
}
