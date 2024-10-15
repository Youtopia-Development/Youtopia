package org.youtopia.data

import kotlinx.serialization.Serializable

@Serializable
sealed interface Skill {
    @Serializable data object Carry : Skill
    @Serializable data object Convert : Skill
    @Serializable data object Creep : Skill
    @Serializable data object Dash : Skill
    @Serializable data object Escape : Skill
    @Serializable data object Float : Skill
    @Serializable data object Fortify : Skill
    @Serializable data object Heal : Skill
    @Serializable data object Hide : Skill
    @Serializable data object Independent : Skill
    @Serializable data object Infiltrate : Skill
    @Serializable data object Persist : Skill
    @Serializable data object Scout : Skill
    @Serializable data object Sneak : Skill
    @Serializable data object Splash : Skill
    @Serializable data object Stiff : Skill
    @Serializable data object Stomp : Skill
    @Serializable data object Surprise : Skill
}
