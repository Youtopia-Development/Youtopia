package org.youtopia.server.gameeffect

import org.youtopia.data.Game
import org.youtopia.data.diff.GameDiff

fun passTurn(
    game: Game,
): GameDiff {
    val nextPlayerId = (game.currentPlayerIndex + 1) % game.players.size
    return GameDiff(
        turnDiff = 1,
        currentPlayerIndex = nextPlayerId,
    )
}
