package org.youtopia.server.gameeffect

import org.youtopia.data.GameEffect
import org.youtopia.data.diff.GameDiff
import org.youtopia.data.diff.TileDiff

fun swapTerrain(
    action: GameEffect.SwapTerrain,
): GameDiff = GameDiff(tilesDiff = mapOf(action.cell to TileDiff(terrain = action.newTerrain)))
