package org.youtopia.server.gameeffect

import org.youtopia.data.GameEffect
import org.youtopia.data.diff.GameDiff
import org.youtopia.data.diff.TileDiff

fun downgradeBuilding(
    action: GameEffect.DowngradeBuilding,
): GameDiff = GameDiff(tilesDiff = mapOf(action.cell to TileDiff(building = action.building)))
