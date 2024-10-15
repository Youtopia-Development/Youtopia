package org.youtopia.data.diff

import kotlinx.serialization.Serializable
import org.youtopia.data.PlayerId
import org.youtopia.data.Position

@Serializable
data class GameDiff(
    val tilesDiff: Map<Position, TileDiff> = emptyMap(),
    val playersDiff: Map<PlayerId, PlayerDiff> = emptyMap(),
    val turnDiff: Int? = null,
    val currentPlayerIndex: Int? = null,
)
