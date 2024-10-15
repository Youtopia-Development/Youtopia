package org.youtopia.effects

import org.youtopia.map.SelectableTiledMap
import org.youtopia.data.GameEffect
import org.youtopia.data.Terrain

fun swapTerrain(
    action: GameEffect.SwapTerrain,
    tileMap: SelectableTiledMap,
) {
    val gameCell = tileMap.getTilesLayerCell(action.cell)
    when (action.newTerrain) {
        Terrain.FIELD,
        Terrain.WATER,
        Terrain.OCEAN -> {
            gameCell.terrainObject = null
        }
        Terrain.FOREST -> {
            gameCell.addTerrain(action.cell, "forest_${action.tribe.name.lowercase()}")
        }
        Terrain.MOUNTAIN -> {
            gameCell.addTerrain(action.cell, "mountain_${action.tribe.name.lowercase()}")
        }
    }
}
