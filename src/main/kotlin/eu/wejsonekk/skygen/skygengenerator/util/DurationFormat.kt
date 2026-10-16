package eu.wejsonekk.skygen.skygengenerator.util

import java.time.Duration

/**
 * Small helper formatting a [Duration] into a compact human readable string
 * (e.g. `1h 5m 12s`).
 */
object DurationFormat {

    fun format(duration: Duration): String {
        val totalSeconds = duration.seconds.coerceAtLeast(0)
        val days = totalSeconds / 86_400
        val hours = (totalSeconds % 86_400) / 3_600
        val minutes = (totalSeconds % 3_600) / 60
        val seconds = totalSeconds % 60

        val builder = StringBuilder()
        if (days > 0) builder.append("${days}d ")
        if (hours > 0) builder.append("${hours}h ")
        if (minutes > 0) builder.append("${minutes}m ")
        if (seconds > 0 || builder.isEmpty()) builder.append("${seconds}s")

        return builder.toString().trim()
    }
}
