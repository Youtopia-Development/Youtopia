package org.youtopia.effects

import org.youtopia.map.GameCell
import org.youtopia.map.Path
import org.youtopia.map.SelectableTiledMap
import org.youtopia.data.GameEffect
import org.youtopia.data.Position

fun buildRoad(
    action: GameEffect.BuildRoad,
    gameSize: Pair<Int, Int>,
    tileMap: SelectableTiledMap,
) {
    val gameCell = tileMap.getTilesLayerCell(action.cell)
    gameCell.isRoad = true
    if (action.cell % gameSize.first != 0) {
        connectToNeighbour(tileMap, action.cell, gameCell, neighbourOffset = -1, direction = 4, neighbourDirection = 0)
    }
    if (action.cell % gameSize.first != 0 && action.cell / gameSize.first != 0) {
        connectToNeighbour(tileMap, action.cell, gameCell, neighbourOffset = -1 - gameSize.first, direction = 3, neighbourDirection = 7)
    }
    if (action.cell / gameSize.first != 0) {
        connectToNeighbour(tileMap, action.cell, gameCell, neighbourOffset = -gameSize.first, direction = 2, neighbourDirection = 6)
    }
    if (action.cell / gameSize.first != 0 && action.cell % gameSize.first != gameSize.first - 1) {
        connectToNeighbour(tileMap, action.cell, gameCell, neighbourOffset = 1 - gameSize.first, direction = 1, neighbourDirection = 5)
    }
    if (action.cell % gameSize.first != gameSize.first - 1) {
        connectToNeighbour(tileMap, action.cell, gameCell, neighbourOffset = 1, direction = 0, neighbourDirection = 4)
    }
    if (action.cell % gameSize.first != gameSize.first - 1 && action.cell / gameSize.first != gameSize.second - 1) {
        connectToNeighbour(tileMap, action.cell, gameCell, neighbourOffset = 1 + gameSize.first, direction = 7, neighbourDirection = 3)
    }
    if (action.cell / gameSize.first != gameSize.second - 1) {
        connectToNeighbour(tileMap, action.cell, gameCell, neighbourOffset = gameSize.first, direction = 6, neighbourDirection = 2)
    }
    if (action.cell % gameSize.first != 0 && action.cell / gameSize.first != gameSize.second - 1) {
        connectToNeighbour(tileMap, action.cell, gameCell, neighbourOffset = -1 + gameSize.first, direction = 5, neighbourDirection = 1)
    }
    if (!gameCell.roadConnected) {
        gameCell.addPath(action.cell, 8, Path.ROAD)
    }
}

fun connectToNeighbour(
    tileMap: SelectableTiledMap,
    pos: Position,
    gameCell: GameCell,
    neighbourOffset: Int,
    direction: Int,
    neighbourDirection: Int,
) {
    val neighbour = tileMap.getTilesLayerCell(pos + neighbourOffset)
    if (neighbour.isRoad) {
        neighbour.roadObjects[8] = null
        neighbour.addPath(pos + neighbourOffset, neighbourDirection, Path.ROAD)
        gameCell.addPath(pos, direction, Path.ROAD)
        neighbour.roadConnected = true
        gameCell.roadConnected = true
    }
    if (neighbour.isPort) {
        neighbour.addPath(pos + neighbourOffset, neighbourDirection, Path.ROUTE)
        gameCell.addPath(pos, direction, Path.ROAD)
        gameCell.roadConnected = true
    }
    if (neighbour.isBridge) {
        gameCell.addPath(pos, direction, Path.ROAD)
        gameCell.roadConnected = true
    }
}
