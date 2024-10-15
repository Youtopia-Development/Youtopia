package org.youtopia.server.gameeffect

import org.youtopia.data.Game
import org.youtopia.data.GameEffect
import org.youtopia.data.diff.GameDiff
import org.youtopia.data.diff.PlayerDiff

fun updateIncome(
    action: GameEffect.UpdateIncome,
    game: Game,
): GameDiff = GameDiff(
    playersDiff = mapOf(
        game.currentPlayerIndex to PlayerDiff(incomeDiff = action.delta)
    )
)
