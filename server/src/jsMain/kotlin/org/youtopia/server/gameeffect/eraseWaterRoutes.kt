package org.youtopia.server.gameeffect

import org.youtopia.data.GameEffect
import org.youtopia.data.diff.GameDiff
import org.youtopia.data.diff.TileDiff

fun eraseWaterRoutes(
    action: GameEffect.EraseWaterRoutes,
): GameDiff = GameDiff(tilesDiff = action.routes.flatten().toSet().associateWith {
    TileDiff(waterRoute = false)
})
