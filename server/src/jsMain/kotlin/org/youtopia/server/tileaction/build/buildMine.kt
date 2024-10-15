package org.youtopia.server.tileaction.build

import org.youtopia.server.api.Server.game
import org.youtopia.data.Building
import org.youtopia.data.GameEffect
import org.youtopia.data.PlayerId
import org.youtopia.data.Position
import org.youtopia.server.utils.neighbours

fun MutableList<GameEffect>.buildMine(
    pos: Position,
    tileOwner: PlayerId?,
    addPopulation: MutableList<GameEffect>.(cell: Position) -> Unit,
) {
    add(GameEffect.UpdateStars(-5))
    add(GameEffect.Build(pos, Building.Mine))
    addPopulation(pos)
    addPopulation(pos)
    for (neighbour in neighbours(pos, game.size)) {
        if (game.tiles[neighbour].owner != tileOwner) continue
        game.tiles[neighbour].building?.let {
            if (it is Building.Forge) {
                add(GameEffect.UpgradeBuilding(neighbour, it.upgrade()))
                addPopulation(neighbour)
                addPopulation(neighbour)
                handleNeighbourMarkets(neighbour, tileOwner)
            }
        }
    }
}
