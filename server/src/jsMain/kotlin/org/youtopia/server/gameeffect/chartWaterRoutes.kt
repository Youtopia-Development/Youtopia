package org.youtopia.server.gameeffect

import org.youtopia.data.GameEffect
import org.youtopia.data.diff.GameDiff
import org.youtopia.data.diff.TileDiff

fun chartWaterRoutes(
    action: GameEffect.ChartWaterRoutes,
): GameDiff = GameDiff(tilesDiff = action.routes.flatten().toSet().associateWith {
    TileDiff(waterRoute = true)
})
