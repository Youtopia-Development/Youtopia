package org.youtopia.effects

import org.youtopia.data.GameEffect
import org.youtopia.ui.CityLabel

fun disconnectFromCapital(
    action: GameEffect.DisconnectFromCapital,
    cityLabels: MutableMap<Int, CityLabel>,
) {
    val cityLabel = cityLabels[action.pos] ?: error("City not found at position ${action.pos}")
    val labelGroup = cityLabel.labelGroup
    labelGroup.removeActorAt(0, false)
}
