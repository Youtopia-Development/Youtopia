package org.youtopia.effects

import org.youtopia.map.SelectableTiledMap
import org.youtopia.data.GameEffect
import org.youtopia.utils.getResourceAtlasName

fun addResource(
    action: GameEffect.AddResource,
    tileMap: SelectableTiledMap,
) {
    val gameCell = tileMap.getTilesLayerCell(action.pos)
    gameCell.addResource(action.pos, getResourceAtlasName(action.resource, action.tribe))
}
