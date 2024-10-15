package org.youtopia.data

import kotlinx.serialization.Serializable

@Serializable
sealed interface TroopAction {
    @Serializable data object Capture : TroopAction
    @Serializable data object Examine : TroopAction
    @Serializable data object Recover : TroopAction
    @Serializable data object Disband : TroopAction
    @Serializable data object HealOthers : TroopAction
}
