package org.youtopia.effects

import org.youtopia.map.SelectableTiledMap
import org.youtopia.data.GameEffect

fun removeResource(
    action: GameEffect.RemoveResource,
    tileMap: SelectableTiledMap,
) {
    val gameCell = tileMap.getTilesLayerCell(action.pos)
    gameCell.resourceObject = null
}
