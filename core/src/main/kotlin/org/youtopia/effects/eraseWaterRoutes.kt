package org.youtopia.effects

import org.youtopia.map.GameCell
import org.youtopia.map.SelectableTiledMap
import org.youtopia.data.GameEffect
import org.youtopia.data.Position

fun eraseWaterRoutes(
    action: GameEffect.EraseWaterRoutes,
    gameSize: Pair<Int, Int>,
    tileMap: SelectableTiledMap,
) {
    for (route in action.routes) {
        for (routeCell in route) {
            val gameCell = tileMap.getTilesLayerCell(routeCell)
            if (routeCell % gameSize.first != 0) {
                removePath(tileMap, routeCell, route, gameCell, offset = -1, direction = 4, neighbourDirection = 0)
            }
            if (routeCell % gameSize.first != 0 && routeCell / gameSize.first != 0) {
                removePath(tileMap, routeCell, route, gameCell, offset = -1 - gameSize.first, direction = 3, neighbourDirection = 7)
            }
            if (routeCell / gameSize.first != 0) {
                removePath(tileMap, routeCell, route, gameCell, offset = -gameSize.first, direction = 2, neighbourDirection = 6)
            }
            if (routeCell / gameSize.first != 0 && routeCell % gameSize.first != gameSize.first - 1) {
                removePath(tileMap, routeCell, route, gameCell, offset = 1 - gameSize.first, direction = 1, neighbourDirection = 5)
            }
            if (routeCell % gameSize.first != gameSize.first - 1) {
                removePath(tileMap, routeCell, route, gameCell, offset = 1, direction = 0, neighbourDirection = 4)
            }
            if (routeCell % gameSize.first != gameSize.first - 1 && routeCell / gameSize.first != gameSize.second - 1) {
                removePath(tileMap, routeCell, route, gameCell, offset = 1 + gameSize.first, direction = 7, neighbourDirection = 3)
            }
            if (routeCell / gameSize.first != gameSize.second - 1) {
                removePath(tileMap, routeCell, route, gameCell, offset = gameSize.first, direction = 6, neighbourDirection = 2)
            }
            if (routeCell % gameSize.first != 0 && routeCell / gameSize.first != gameSize.second - 1) {
                removePath(tileMap, routeCell, route, gameCell, offset = -1 + gameSize.first, direction = 5, neighbourDirection = 1)
            }
        }
    }
}

fun removePath(
    tileMap: SelectableTiledMap,
    pos: Position,
    route: List<Position>,
    gameCell: GameCell,
    offset: Int,
    direction: Int,
    neighbourDirection: Int,
) {
    val neighbourCell = tileMap.getTilesLayerCell(pos + offset)
    if (pos + offset in route) {
        neighbourCell.roadObjects[neighbourDirection] = null
        gameCell.roadObjects[direction] = null
    }
}
