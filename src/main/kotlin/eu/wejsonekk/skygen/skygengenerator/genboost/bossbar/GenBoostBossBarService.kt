package eu.wejsonekk.skygen.skygengenerator.genboost.bossbar

import eu.wejsonekk.skygen.skygengenerator.api.genboost.GenBoostType
import eu.wejsonekk.skygen.skygengenerator.genboost.model.ActiveGenBoost
import eu.wejsonekk.skygen.skygengenerator.util.DurationFormat
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.time.Duration
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Owns every Adventure [BossBar] used by the gen boost system:
 * one bar per active boost plus a shared lottery countdown bar.
 *
 * All methods must be called from the main thread.
 */
class GenBoostBossBarService(
    private val miniMessage: MiniMessage,
) {

    private val boostBars = ConcurrentHashMap<UUID, BossBar>()

    private val countdownBar: BossBar = BossBar.bossBar(
        Component.empty(),
        1.0f,
        BossBar.Color.YELLOW,
        BossBar.Overlay.PROGRESS,
    )

    fun showBoost(active: ActiveGenBoost, viewers: Collection<Player>, now: Instant) {
        val bar = this.boostBars.computeIfAbsent(active.id) {
            BossBar.bossBar(this.title(active, now), active.progress(now), this.color(active.type), BossBar.Overlay.PROGRESS)
        }
        bar.name(this.title(active, now))
        bar.progress(active.progress(now))
        viewers.forEach { it.showBossBar(bar) }
    }

    fun updateBoost(active: ActiveGenBoost, now: Instant) {
        val bar = this.boostBars[active.id] ?: return
        bar.name(this.title(active, now))
        bar.progress(active.progress(now))
    }

    fun hideBoost(active: ActiveGenBoost, viewers: Collection<Player> = Bukkit.getOnlinePlayers()) {
        val bar = this.boostBars.remove(active.id) ?: return
        viewers.forEach { it.hideBossBar(bar) }
    }

    fun hideBoostFor(player: Player, active: ActiveGenBoost) {
        this.boostBars[active.id]?.let { player.hideBossBar(it) }
    }

    fun showCountdown(remaining: Duration, total: Duration, viewers: Collection<Player>) {
        this.countdownBar.name(this.countdownTitle(remaining))
        this.countdownBar.progress(this.progress(remaining, total))
        viewers.forEach { it.showBossBar(this.countdownBar) }
    }

    fun updateCountdown(remaining: Duration, total: Duration, viewers: Collection<Player>) {
        this.countdownBar.name(this.countdownTitle(remaining))
        this.countdownBar.progress(this.progress(remaining, total))
        viewers.forEach { it.showBossBar(this.countdownBar) }
    }

    fun hideCountdown(viewers: Collection<Player> = Bukkit.getOnlinePlayers()) {
        viewers.forEach { it.hideBossBar(this.countdownBar) }
    }

    private fun title(active: ActiveGenBoost, now: Instant): Component {
        val remaining = DurationFormat.format(active.remaining(now))
        val prefix = if (active.type == GenBoostType.GLOBAL) "Globalny" else "Personalny"
        return this.miniMessage.deserialize(
            "<gradient:#ffb300:#ff6f00>$prefix GenBoost</gradient> <dark_gray>|</dark_gray> " +
                "<white>${active.name} <gray>x${active.multiplier} <dark_gray>|</dark_gray> <yellow>$remaining",
        )
    }

    private fun countdownTitle(remaining: Duration): Component = this.miniMessage.deserialize(
        "<gold>Nastepne losowanie GenBoost za <white>${DurationFormat.format(remaining)}",
    )

    private fun color(type: GenBoostType): BossBar.Color =
        if (type == GenBoostType.GLOBAL) BossBar.Color.BLUE else BossBar.Color.GREEN

    private fun progress(remaining: Duration, total: Duration): Float {
        val totalMillis = total.toMillis().toDouble().coerceAtLeast(1.0)
        return (remaining.toMillis() / totalMillis).coerceIn(0.0, 1.0).toFloat()
    }
}
