package org.youtopia.server.gameeffect

import org.youtopia.data.Game
import org.youtopia.data.GameEffect
import org.youtopia.data.diff.CityDiff
import org.youtopia.data.diff.GameDiff
import org.youtopia.data.diff.PlayerDiff

fun disconnectFromCapital(
    action: GameEffect.DisconnectFromCapital,
    game: Game,
): GameDiff {
    val owner = game.tiles[action.pos].owner ?: error("City at ${action.pos} does not have an owner.")
    return GameDiff(
        playersDiff = mapOf(
            owner to PlayerDiff(
                citiesModified = mapOf(action.pos to CityDiff(connectedToCapital = false))
            )
        )
    )
}
