package org.youtopia.effects

import org.youtopia.map.GameCell
import org.youtopia.map.Path
import org.youtopia.map.SelectableTiledMap
import org.youtopia.data.Building
import org.youtopia.data.GameEffect
import org.youtopia.data.Position

fun build(
    action: GameEffect.Build,
    gameSize: Pair<Int, Int>,
    tileMap: SelectableTiledMap,
) {
    val gameCell = tileMap.getTilesLayerCell(action.cell)
    if (action.building is Building.Market) {
        gameCell.addMarket(action.cell, action.building as Building.Market)
        gameCell.resourceObject = null
        return
    }
    gameCell.addBuilding(action.cell, action.building)
    if (action.building is Building.Port) handlePortConstruction(gameCell, action.cell, gameSize, tileMap)
    if (action.building is Building.Bridge) handleBridgeConstruction(gameCell, action.cell, gameSize, tileMap)
    gameCell.resourceObject = null
}

fun handlePortConstruction(
    gameCell: GameCell,
    pos: Position,
    gameSize: Pair<Int, Int>,
    tileMap: SelectableTiledMap,
) {
    gameCell.isPort = true
    if (pos % gameSize.first != 0) {
        connectToPort(tileMap, gameCell, pos, offset = -1, direction = 4, neighbourDirection = 0)
    }
    if (pos % gameSize.first != 0 && pos / gameSize.first != 0) {
        connectToPort(tileMap, gameCell, pos, offset = -1 - gameSize.first, direction = 3, neighbourDirection = 7)
    }
    if (pos / gameSize.first != 0) {
        connectToPort(tileMap, gameCell, pos, offset = -gameSize.first, direction = 2, neighbourDirection = 6)
    }
    if (pos / gameSize.first != 0 && pos % gameSize.first != gameSize.first - 1) {
        connectToPort(tileMap, gameCell, pos, offset = 1 - gameSize.first, direction = 1, neighbourDirection = 5)
    }
    if (pos % gameSize.first != gameSize.first - 1) {
        connectToPort(tileMap, gameCell, pos, offset = 1, direction = 0, neighbourDirection = 4)
    }
    if (pos % gameSize.first != gameSize.first - 1 && pos / gameSize.first != gameSize.second - 1) {
        connectToPort(tileMap, gameCell, pos, offset = 1 + gameSize.first, direction = 7, neighbourDirection = 3)
    }
    if (pos / gameSize.first != gameSize.second - 1) {
        connectToPort(tileMap, gameCell, pos, offset = gameSize.first, direction = 6, neighbourDirection = 2)
    }
    if (pos % gameSize.first != 0 && pos / gameSize.first != gameSize.second - 1) {
        connectToPort(tileMap, gameCell, pos, offset = -1 + gameSize.first, direction = 5, neighbourDirection = 1)
    }
}

private fun connectToPort(
    tileMap: SelectableTiledMap,
    gameCell: GameCell,
    pos: Position,
    offset: Int,
    direction: Int,
    neighbourDirection: Int,
) {
    val neighbourCell = tileMap.getTilesLayerCell(pos + offset)
    if (neighbourCell.isRoad) {
        neighbourCell.roadObjects[8] = null
        neighbourCell.addPath(pos + offset, neighbourDirection, Path.ROAD)
        gameCell.addPath(pos, direction, Path.ROUTE)
        neighbourCell.roadConnected = true
        gameCell.roadConnected = true
    }
    if (neighbourCell.isPort) {
        neighbourCell.addPath(pos + offset, neighbourDirection, Path.ROUTE)
        gameCell.addPath(pos, direction, Path.ROUTE)
        gameCell.roadConnected = true
    }
    if (neighbourCell.isBridge) {
        gameCell.addPath(pos, direction, Path.ROUTE)
        gameCell.roadConnected = true
    }
}

fun handleBridgeConstruction(
    gameCell: GameCell,
    pos: Position,
    gameSize: Pair<Int, Int>,
    tileMap: SelectableTiledMap,
) {
    gameCell.isBridge = true
    if (pos % gameSize.first != 0) {
        connectToBridge(tileMap, pos, offset = -1, direction = 0)
    }
    if (pos % gameSize.first != 0 && pos / gameSize.first != 0) {
        connectToBridge(tileMap, pos, offset = -1 - gameSize.first, direction = 7)
    }
    if (pos / gameSize.first != 0) {
        connectToBridge(tileMap, pos, offset = -gameSize.first, direction = 6)
    }
    if (pos / gameSize.first != 0 && pos % gameSize.first != gameSize.first - 1) {
        connectToBridge(tileMap, pos, offset = 1 - gameSize.first, direction = 5)
    }
    if (pos % gameSize.first != gameSize.first - 1) {
        connectToBridge(tileMap, pos, offset = 1, direction = 4)
    }
    if (pos % gameSize.first != gameSize.first - 1 && pos / gameSize.first != gameSize.second - 1) {
        connectToBridge(tileMap, pos, offset = 1 + gameSize.first, direction = 3)
    }
    if (pos / gameSize.first != gameSize.second - 1) {
        connectToBridge(tileMap, pos, offset = gameSize.first, direction = 2)
    }
    if (pos % gameSize.first != 0 && pos / gameSize.first != gameSize.second - 1) {
        connectToBridge(tileMap, pos, offset = -1 + gameSize.first, direction = 1)
    }
}

private fun connectToBridge(
    tileMap: SelectableTiledMap,
    pos: Position,
    offset: Int,
    direction: Int,
) {
    val cell = tileMap.getTilesLayerCell(pos + offset)
    if (cell.isRoad) {
        cell.roadObjects[8] = null
        cell.addPath(pos + offset, direction, Path.ROAD)
        cell.roadConnected = true
    }
    if (cell.isPort) {
        cell.addPath(pos + offset, direction, Path.ROUTE)
    }
}
