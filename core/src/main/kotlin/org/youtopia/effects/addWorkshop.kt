package org.youtopia.effects

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.youtopia.map.SelectableTiledMap
import org.youtopia.data.GameEffect
import org.youtopia.ui.CityLabel
import org.youtopia.ui.UiStage

fun addWorkshop(
    action: GameEffect.AddWorkshop,
    tileMap: SelectableTiledMap,
    cityLabels: MutableMap<Int, CityLabel>,
    uiStage: UiStage,
    stars: MutableStateFlow<Int>,
    income: MutableStateFlow<Int>,
) {
    val cityLabel = cityLabels[action.pos] ?: error("City not found at position ${action.pos}")
    val incomeLabel = cityLabel.incomeLabel
    incomeLabel.setText(incomeLabel.text.toString().toInt() + 1)

    income.update { it + 1 }
    uiStage.starsLabel.setText("${stars.value} (+${income.value})")

    val gameCell = tileMap.getTilesLayerCell(action.pos)
    gameCell.addCity(action.pos, action.city)
}
