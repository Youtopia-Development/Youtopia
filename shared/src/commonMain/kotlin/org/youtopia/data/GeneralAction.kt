package org.youtopia.data

import kotlinx.serialization.Serializable

@Serializable
sealed interface GeneralAction {

    @Serializable
    data object PassTurn : GeneralAction
}
