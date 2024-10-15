package org.youtopia.server.gameeffect

import org.youtopia.data.GameEffect
import org.youtopia.data.diff.GameDiff
import org.youtopia.data.diff.TileDiff

fun addResource(
    action: GameEffect.AddResource,
): GameDiff = GameDiff(tilesDiff = mapOf(action.pos to TileDiff(resource = action.resource)))
