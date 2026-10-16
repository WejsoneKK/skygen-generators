package eu.wejsonekk.skygen.skygengenerator.gui

import org.bukkit.entity.Player
import java.util.WeakHashMap

/**
 * Per player stack of previously opened [AbstractGui]s used by the "back"
 * navigation. Weak keys so disconnected players are not leaked.
 */
object GuiHistory {

    private val histories = WeakHashMap<Player, ArrayDeque<AbstractGui>>()

    fun push(player: Player, gui: AbstractGui) {
        this.histories.getOrPut(player) { ArrayDeque() }.addLast(gui)
    }

    fun pop(player: Player): AbstractGui? {
        val stack = this.histories[player] ?: return null
        val gui = stack.removeLastOrNull()
        if (stack.isEmpty()) {
            this.histories.remove(player)
        }
        return gui
    }

    fun clear(player: Player) {
        this.histories.remove(player)
    }
}
