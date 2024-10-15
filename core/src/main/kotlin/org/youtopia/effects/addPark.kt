package org.youtopia.effects

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.youtopia.map.SelectableTiledMap
import org.youtopia.data.GameEffect
import org.youtopia.ui.CityLabel
import org.youtopia.ui.UiStage

fun addPark(
    action: GameEffect.AddPark,
    tileMap: SelectableTiledMap,
    cityLabels: MutableMap<Int, CityLabel>,
    uiStage: UiStage,
    score: MutableStateFlow<Int>,
    stars: MutableStateFlow<Int>,
    income: MutableStateFlow<Int>,
) {
    val cityLabel = cityLabels[action.pos] ?: error("City not found at position ${action.pos}")
    val incomeLabel = cityLabel.incomeLabel
    incomeLabel.setText(incomeLabel.text.toString().toInt() + 1)

    income.update { it + 1 }
    uiStage.starsLabel.setText("${stars.value} (+${income.value})")

    score.update { it + 250 }
    uiStage.scoreLabel.setText(score.value)

    val gameCell = tileMap.getTilesLayerCell(action.pos)
    gameCell.addCity(action.pos, action.city)
}
