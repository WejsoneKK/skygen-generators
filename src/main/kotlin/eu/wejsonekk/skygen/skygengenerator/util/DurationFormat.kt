package eu.wejsonekk.skygen.skygengenerator.util

import java.time.Duration

/**
 * Small helper formatting a [Duration] into a compact human readable string
 * (e.g. `1h 5m 12s 500ms`).
 *
 * When [format] is called with `removeMillis = true` the millisecond part is
 * omitted and sub-second durations are rendered as `<1s` (instead of `0s`).
 */
object DurationFormat {

    fun format(duration: Duration, removeMillis: Boolean = false): String {
        val millis = duration.toMillis().coerceAtLeast(0)
        if (millis == 0L) {
            return "0s"
        }

        val days = millis / 86_400_000
        val hours = (millis % 86_400_000) / 3_600_000
        val minutes = (millis % 3_600_000) / 60_000
        val seconds = (millis % 60_000) / 1_000
        val millisPart = millis % 1_000

        val builder = StringBuilder()
        if (days > 0) builder.append("${days}d ")
        if (hours > 0) builder.append("${hours}h ")
        if (minutes > 0) builder.append("${minutes}m ")
        if (seconds > 0) builder.append("${seconds}s")

        if (builder.isEmpty()) {
            return if (removeMillis) "<1s" else "${millisPart}ms"
        }

        if (!removeMillis && millisPart > 0) {
            builder.append(' ').append("${millisPart}ms")
        }

        return builder.toString().trim()
    }
}
