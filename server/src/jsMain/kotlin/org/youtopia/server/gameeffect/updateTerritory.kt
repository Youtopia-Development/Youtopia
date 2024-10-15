package org.youtopia.server.gameeffect

import org.youtopia.data.Game
import org.youtopia.data.GameEffect
import org.youtopia.data.diff.CityDiff
import org.youtopia.data.diff.GameDiff
import org.youtopia.data.diff.PlayerDiff
import org.youtopia.data.diff.TileDiff

fun updateTerritory(
    action: GameEffect.UpdateTerritory,
    game: Game,
): GameDiff {
    val owner = game.tiles[action.city].owner ?: error("City does not have an owner.")
    return GameDiff(
        playersDiff = mapOf(
            owner to PlayerDiff(
                citiesModified = mapOf(action.city to CityDiff(territory = action.territory)),
                scoreDiff = (action.territory.size - 9) * 20,
            )
        ),
        tilesDiff = action.territory.associateWith {
            TileDiff(city = action.city, owner = owner)
        },
    )
}
