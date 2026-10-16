package eu.wejsonekk.skygen.skygengenerator.generator.hologram

/**
 * Selects which hologram backend is used.
 */
enum class HologramProvider {
    /** Use FancyHolograms when installed, otherwise the built in [TextDisplayHologramProvider]. */
    AUTO,

    /** Always use the built in [TextDisplayHologramProvider]. */
    TEXT_DISPLAY,

    /** Always use FancyHolograms (falls back to [TEXT_DISPLAY] when unavailable). */
    FANCY_HOLOGRAMS,
}
