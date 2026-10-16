package eu.wejsonekk.skygen.skygengenerator.genboost.listener

import eu.wejsonekk.skygen.skygengenerator.genboost.manager.GenBoostManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

/**
 * Makes sure a joining player immediately sees the active global boosts and the
 * lottery countdown boss bar.
 */
class GenBoostJoinListener(
    private val manager: GenBoostManager,
) : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        this.manager.onPlayerJoin(event.player)
    }
}
