package org.youtopia.server.gameeffect

import org.youtopia.data.Building
import org.youtopia.data.Game
import org.youtopia.data.GameEffect
import org.youtopia.data.diff.GameDiff
import org.youtopia.data.diff.TileDiff

fun addWall(
    action: GameEffect.AddWall,
    game: Game,
): GameDiff {
    val city = game.tiles[action.pos].building as Building.City
    return GameDiff(
        tilesDiff = mapOf(
            action.pos to TileDiff(
                building = city.copy(
                    hasWalls = true
                )
            )
        ),
    )
}
