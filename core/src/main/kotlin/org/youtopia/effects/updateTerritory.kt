package org.youtopia.effects

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.youtopia.map.BorderDirection
import org.youtopia.map.SelectableTiledMap
import org.youtopia.data.GameEffect
import org.youtopia.ui.UiStage

fun updateTerritory(
    action: GameEffect.UpdateTerritory,
    gameSize: Pair<Int, Int>,
    tileMap: SelectableTiledMap,
    uiStage: UiStage,
    score: MutableStateFlow<Int>,
) {
    val cityCell = tileMap.getTilesLayerCell(action.city)

    for (pos in SelectableTiledMap.round(action.city, 3, gameSize)) {
        val gameCell = tileMap.getTilesLayerCell(pos)
        gameCell.topLeftBorderObject = null
        gameCell.topRightBorderObject = null
        gameCell.bottomLeftBorderObject = null
        gameCell.bottomRightBorderObject = null

        if (pos in action.territory) {
            gameCell.owner = cityCell.owner
            gameCell.city = action.city
        }
    }

    score.update { it + (action.territory.size - 9) * 20 }
    uiStage.scoreLabel.setText(score.value)

    for (pos in SelectableTiledMap.round(action.city, 3, gameSize)) {
        val gameCell = tileMap.getTilesLayerCell(pos)
        if (gameCell.owner == null) continue

        if (pos % gameSize.first == 0 || (tileMap.getTilesLayerCell(pos - 1)).owner != gameCell.owner) {
            gameCell.addBorderMapObject(pos, BorderDirection.TOP_LEFT)
        }
        if (pos % gameSize.first == gameSize.first - 1 || (tileMap.getTilesLayerCell(pos + 1)).owner != gameCell.owner) {
            gameCell.addBorderMapObject(pos, BorderDirection.BOTTOM_RIGHT)
        }
        if (pos / gameSize.first == 0 || (tileMap.getTilesLayerCell(pos - gameSize.first)).owner != gameCell.owner) {
            gameCell.addBorderMapObject(pos, BorderDirection.BOTTOM_LEFT)
        }
        if (pos / gameSize.first == gameSize.second - 1 || (tileMap.getTilesLayerCell(pos + gameSize.first)).owner != gameCell.owner) {
            gameCell.addBorderMapObject(pos, BorderDirection.TOP_RIGHT)
        }
    }
}
