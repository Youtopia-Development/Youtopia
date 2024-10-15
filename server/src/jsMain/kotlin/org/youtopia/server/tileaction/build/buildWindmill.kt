package org.youtopia.server.tileaction.build

import org.youtopia.server.api.Server.game
import org.youtopia.data.Building
import org.youtopia.data.GameEffect
import org.youtopia.data.PlayerId
import org.youtopia.data.Position
import org.youtopia.data.Tile
import org.youtopia.server.utils.neighbours

fun MutableList<GameEffect>.buildWindmill(
    tile: Tile,
    pos: Position,
    tileOwner: PlayerId?,
    addPopulation: MutableList<GameEffect>.(cell: Position) -> Unit,
) {
    add(GameEffect.UpdateStars(-5))
    if (tile.resource != null) {
        add(GameEffect.RemoveResource(pos))
    }
    var level = 0
    for (neighbour in neighbours(pos, game.size)) {
        if (game.tiles[neighbour].owner != tileOwner) continue
        game.tiles[neighbour].building?.let {
            if (it is Building.Farm) {
                level++
            }
        }
    }
    add(GameEffect.Build(pos, Building.Windmill(level)))
    repeat(level) {
        addPopulation(pos)
    }
    for (neighbour in neighbours(pos, game.size)) {
        if (game.tiles[neighbour].owner != tileOwner) continue
        game.tiles[neighbour].building?.let { building ->
            if (building is Building.Market) {
                repeat(level) {
                    add(GameEffect.UpgradeBuilding(neighbour, building.upgrade().setStands(nearWindmill = true)))
                    add(GameEffect.UpdateIncome(1))
                }
            }
        }
    }
}
