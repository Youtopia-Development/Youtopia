package org.youtopia.effects

import org.youtopia.map.SelectableTiledMap
import org.youtopia.data.GameEffect

fun addWall(
    action: GameEffect.AddWall,
    tileMap: SelectableTiledMap,
) {
    val gameCell = tileMap.getTilesLayerCell(action.pos)
    gameCell.addCity(action.pos, action.city)
}
