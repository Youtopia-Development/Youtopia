package org.youtopia.server.gameeffect

import org.youtopia.data.Building
import org.youtopia.data.Game
import org.youtopia.data.GameEffect
import org.youtopia.data.diff.CityDiff
import org.youtopia.data.diff.GameDiff
import org.youtopia.data.diff.PlayerDiff
import org.youtopia.data.diff.TileDiff

fun addWorkshop(
    action: GameEffect.AddWorkshop,
    game: Game,
): GameDiff {
    val city = game.tiles[action.pos].building as Building.City
    val owner = game.tiles[action.pos].owner ?: error("City at position ${action.pos} doesn't belong to a player")
    return GameDiff(
        tilesDiff = mapOf(
            action.pos to TileDiff(
                building = city.copy(
                    hasWorkshop = true
                )
            )
        ),
        playersDiff = mapOf(owner to PlayerDiff(
            citiesModified = mapOf(action.pos to CityDiff(incomeDiff = 1)),
            incomeDiff = 1,
        )
        ),
    )
}
