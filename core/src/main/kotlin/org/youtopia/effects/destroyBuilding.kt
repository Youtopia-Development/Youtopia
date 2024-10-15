package org.youtopia.effects

import org.youtopia.map.Path
import org.youtopia.map.SelectableTiledMap
import org.youtopia.data.GameEffect
import org.youtopia.utils.getResourceAtlasName

fun destroyBuilding(
    action: GameEffect.DestroyBuilding,
    gameSize: Pair<Int, Int>,
    tileMap: SelectableTiledMap,
) {
    val gameCell = tileMap.getTilesLayerCell(action.cell)
    val resource = action.underlyingResource
    val tribe = action.underlyingResourceTribe
    gameCell.buildingPartObjects.clear()
    if (resource != null && tribe != null) {
        gameCell.addResource(action.cell, getResourceAtlasName(resource, tribe))
    }
    if (gameCell.isBridge || gameCell.isPort) {
        gameCell.apply {
            isBridge = false
            isPort = false
            roadConnected = false
            roadObjects.replaceAll { null }
        }
        if (action.cell % gameSize.first != 0) {
            removePath(action, tileMap, offset = -1, direction = 0)
        }
        if (action.cell % gameSize.first != 0 && action.cell / gameSize.first != 0) {
            removePath(action, tileMap, offset = -1 - gameSize.first, direction = 7)
        }
        if (action.cell / gameSize.first != 0) {
            removePath(action, tileMap, offset = -gameSize.first, direction = 6)
        }
        if (action.cell / gameSize.first != 0 && action.cell % gameSize.first != gameSize.first - 1) {
            removePath(action, tileMap, offset = 1 - gameSize.first, direction = 5)
        }
        if (action.cell % gameSize.first != gameSize.first - 1) {
            removePath(action, tileMap, offset = 1, direction = 4)
        }
        if (action.cell % gameSize.first != gameSize.first - 1 && action.cell / gameSize.first != gameSize.second - 1) {
            removePath(action, tileMap, offset = 1 + gameSize.first, direction = 3)
        }
        if (action.cell / gameSize.first != gameSize.second - 1) {
            removePath(action, tileMap, offset = gameSize.first, direction = 2)
        }
        if (action.cell % gameSize.first != 0 && action.cell / gameSize.first != gameSize.second - 1) {
            removePath(action, tileMap, offset = -1 + gameSize.first, direction = 1)
        }
    }
    gameCell.buildingObject = null
}

private fun removePath(
    action: GameEffect.DestroyBuilding,
    tileMap: SelectableTiledMap,
    offset: Int,
    direction: Int,
) {
    val cell = tileMap.getTilesLayerCell(action.cell + offset)
    if (cell.roadConnected) {
        cell.roadObjects[direction] = null
        if (cell.roadObjects.filterNotNull().isEmpty() && !cell.isBridge && !cell.isPort) {
            cell.addPath(action.cell + offset, 8, Path.ROAD)
            cell.roadConnected = false
        }
    }
}
