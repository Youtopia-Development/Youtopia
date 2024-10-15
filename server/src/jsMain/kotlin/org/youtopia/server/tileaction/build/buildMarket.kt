package org.youtopia.server.tileaction.build

import org.youtopia.server.api.Server.game
import org.youtopia.data.Building
import org.youtopia.data.GameEffect
import org.youtopia.data.PlayerId
import org.youtopia.data.Position
import org.youtopia.data.Tile
import org.youtopia.server.utils.neighbours

fun MutableList<GameEffect>.buildMarket(
    tile: Tile,
    pos: Position,
    tileOwner: PlayerId?,
) {
    add(GameEffect.UpdateStars(-5))
    if (tile.resource != null) {
        add(GameEffect.RemoveResource(pos))
    }
    var level = 0
    var nearSawmill = false
    var nearWindmill = false
    var nearForge = false
    for (neighbour in neighbours(pos, game.size)) {
        if (game.tiles[neighbour].owner != tileOwner) continue
        game.tiles[neighbour].building?.let {
            when (it) {
                is Building.Sawmill -> {
                    level += it.level
                    nearSawmill = true
                }
                is Building.Windmill -> {
                    level += it.level
                    nearWindmill = true
                }
                is Building.Forge -> {
                    level += it.level
                    nearForge = true
                }
                else -> Unit
            }
        }
    }
    add(GameEffect.Build(pos, Building.Market(level, nearSawmill, nearWindmill, nearForge)))
    add(GameEffect.UpdateIncome(level))
}
