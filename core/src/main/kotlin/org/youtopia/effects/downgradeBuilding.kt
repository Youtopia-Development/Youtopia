package org.youtopia.effects

import org.youtopia.map.SelectableTiledMap
import org.youtopia.data.Building
import org.youtopia.data.GameEffect

fun downgradeBuilding(
    action: GameEffect.DowngradeBuilding,
    tileMap: SelectableTiledMap,
) {
    val gameCell = tileMap.getTilesLayerCell(action.cell)
    if (action.building is Building.Market) {
        gameCell.addMarket(action.cell, action.building as Building.Market)
        return
    }
    gameCell.addBuilding(action.cell, action.building)
}
