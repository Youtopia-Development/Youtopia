package org.youtopia.server.tileaction.build

import org.youtopia.server.api.Server.game
import org.youtopia.data.Building
import org.youtopia.data.GameEffect
import org.youtopia.data.PlayerId
import org.youtopia.data.Position
import org.youtopia.data.Tile
import org.youtopia.server.utils.neighbours

fun MutableList<GameEffect>.buildLumberHut(
    tile: Tile,
    pos: Position,
    tileOwner: PlayerId?,
    addPopulation: MutableList<GameEffect>.(cell: Position) -> Unit,
) {
    add(GameEffect.UpdateStars(-3))
    if (tile.resource != null) {
        add(GameEffect.RemoveResource(pos))
    }
    add(GameEffect.Build(pos, Building.LumberHut))
    addPopulation(pos)
    for (neighbour in neighbours(pos, game.size)) {
        if (game.tiles[neighbour].owner != tileOwner) continue
        game.tiles[neighbour].building?.let {
            if (it is Building.Sawmill) {
                add(GameEffect.UpgradeBuilding(neighbour, it.upgrade()))
                addPopulation(neighbour)
                handleNeighbourMarkets(neighbour, tileOwner)
            }
        }
    }
}
