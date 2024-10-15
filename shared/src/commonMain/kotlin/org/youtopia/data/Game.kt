package org.youtopia.data

import kotlinx.serialization.Serializable
import org.youtopia.data.diff.GameDiff

@Serializable
data class Game(
    val tiles: List<Tile>,
    val size: Pair<Int, Int>,
    val players: List<Player>,
    val turn: Int,
    val currentPlayerIndex: Int,
) {
    fun update(diff: GameDiff): Game = Game(
        tiles = if (diff.tilesDiff.isNotEmpty()) tiles.toMutableList().apply {
            diff.tilesDiff.forEach { this[it.key] = this[it.key].update(it.value) }
        }.toList() else tiles,
        size = size,
        players = if (diff.playersDiff.isNotEmpty()) players.toMutableList().apply {
            diff.playersDiff.forEach { this[it.key] = this[it.key].update(it.value) }
        }.toList() else players,
        turn = turn + (diff.turnDiff ?: 0),
        currentPlayerIndex = diff.currentPlayerIndex ?: currentPlayerIndex,
    )
}
