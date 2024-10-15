package org.youtopia.server.tileaction.build

import org.youtopia.server.api.Server.game
import org.youtopia.data.Building
import org.youtopia.data.GameEffect
import org.youtopia.data.PlayerId
import org.youtopia.data.Position
import org.youtopia.server.utils.neighbours

fun MutableList<GameEffect>.handleNeighbourMarkets(
    neighbour: Position,
    tileOwner: PlayerId?,
) {
    for (marketNeighbour in neighbours(neighbour, game.size)) {
        if (game.tiles[marketNeighbour].owner != tileOwner) continue
        game.tiles[marketNeighbour].building?.let { marketNeighbourBuilding ->
            if (marketNeighbourBuilding is Building.Market) {
                add(GameEffect.UpgradeBuilding(marketNeighbour, marketNeighbourBuilding.upgrade()))
                add(GameEffect.UpdateIncome(1))
            }
        }
    }
}
