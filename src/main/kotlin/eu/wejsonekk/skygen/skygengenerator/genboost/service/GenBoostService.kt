package eu.wejsonekk.skygen.skygengenerator.genboost.service

import eu.wejsonekk.skygen.skygengenerator.api.event.GenBoostReceiveEvent
import eu.wejsonekk.skygen.skygengenerator.api.genboost.GenBoostType
import eu.wejsonekk.skygen.skygengenerator.genboost.adapter.GenBoostItemAdapter
import eu.wejsonekk.skygen.skygengenerator.genboost.manager.GenBoostManager
import eu.wejsonekk.skygen.skygengenerator.genboost.model.GenBoostImpl
import eu.wejsonekk.skygen.skygengenerator.notice.NoticeService
import eu.wejsonekk.skygen.skygengenerator.util.DurationFormat
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.time.Duration

/**
 * High level entry points used by commands and listeners for the gen boost system.
 */
class GenBoostService(
    private val manager: GenBoostManager,
    private val itemAdapter: GenBoostItemAdapter,
    private val noticeService: NoticeService,
) {

    /** Gives the [player] a physical boost item that can be activated by right clicking. */
    fun giveItem(
        player: Player,
        type: GenBoostType,
        multiplier: Double,
        duration: Duration,
        name: String,
    ): Boolean {
        val item = this.itemAdapter.create(type, multiplier, duration, name)

        val boost = GenBoostImpl(
            type = type,
            multiplier = multiplier,
            duration = duration,
            ownerId = if (type == GenBoostType.PERSONAL) player.uniqueId else null,
            name = name,
        )

        val event = GenBoostReceiveEvent(player, boost, item)
        Bukkit.getPluginManager().callEvent(event)
        if (event.isCancelled) {
            return false
        }

        val leftover = player.inventory.addItem(event.item)
        leftover.values.forEach { player.world.dropItemNaturally(player.location, it) }

        this.noticeService.create()
            .viewer(player)
            .notice { it.genBoostItemReceived }
            .placeholder("{name}", name)
            .placeholder("{type}", type.name)
            .placeholder("{multiplier}", multiplier.toString())
            .placeholder("{duration}", DurationFormat.format(duration))
            .send()
        return true
    }

    /** Immediately activates a boost for every player (no physical item). */
    fun giveAll(multiplier: Double, duration: Duration, name: String): Boolean =
        this.manager.activate(GenBoostType.GLOBAL, multiplier, duration, null, name) != null

    /** Activates the boost stored inside [item] for [player]. Consumes the item on success. */
    fun activateFromItem(player: Player, item: ItemStack): Boolean {
        val data = this.itemAdapter.read(item) ?: return false

        val ownerId = if (data.type == GenBoostType.PERSONAL) player.uniqueId else null
        val active = this.manager.activate(
            data.type,
            data.multiplier,
            data.duration,
            ownerId,
            data.name,
        ) ?: return false

        item.amount -= 1

        this.noticeService.create()
            .viewer(player)
            .notice { it.genBoostActivated }
            .placeholder("{name}", active.name)
            .placeholder("{multiplier}", active.multiplier.toString())
            .placeholder("{duration}", DurationFormat.format(active.duration))
            .send()
        return true
    }

    /** Stores [item] as the visual representation of physical boosts of [type]. */
    fun setTemplate(type: GenBoostType, item: ItemStack) {
        this.itemAdapter.applyTemplate(type, item)
    }
}
