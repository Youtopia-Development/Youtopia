package org.youtopia.server.gameeffect

import org.youtopia.data.Building
import org.youtopia.data.Game
import org.youtopia.data.GameEffect
import org.youtopia.data.diff.GameDiff
import org.youtopia.data.diff.TileDiff

fun decrementTempleCounter(
    action: GameEffect.DecrementTempleCounter,
    game: Game,
): GameDiff {
    val temple = game.tiles[action.pos].building as Building.Temple<*>
    return GameDiff(
        tilesDiff = mapOf(
            action.pos to TileDiff(
                building = temple.decrementCounter()
            )
        ),
    )
}
