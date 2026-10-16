package eu.wejsonekk.skygen.skygengenerator.api.genboost

/**
 * Scope of a gen boost.
 */
enum class GenBoostType {

    /** Applies to every generator and is shown to every player. */
    GLOBAL,

    /** Applies to the generators owned by [GenBoost.ownerId] and is shown to that player. */
    PERSONAL,
}
