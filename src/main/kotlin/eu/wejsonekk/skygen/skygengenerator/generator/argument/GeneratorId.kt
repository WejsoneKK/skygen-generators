package eu.wejsonekk.skygen.skygengenerator.generator.argument

/**
 * Strongly typed wrapper around a generator id, used as a dedicated LiteCommands
 * argument so the framework can suggest existing generator ids.
 */
data class GeneratorId(val value: String) {

    override fun toString(): String = this.value
}
