package org.youtopia.effects

import ktx.scene2d.image
import ktx.scene2d.scene2d
import org.youtopia.data.GameEffect
import org.youtopia.ui.CityLabel
import org.youtopia.utils.mapUiAtlas

fun connectToCapital(
    action: GameEffect.ConnectToCapital,
    cityLabels: MutableMap<Int, CityLabel>,
) {
    val cityLabel = cityLabels[action.pos] ?: error("City not found at position ${action.pos}")
    val labelGroup = cityLabel.labelGroup
    labelGroup.addActorAt(0, scene2d.image(mapUiAtlas.findRegion("connected")))
}
