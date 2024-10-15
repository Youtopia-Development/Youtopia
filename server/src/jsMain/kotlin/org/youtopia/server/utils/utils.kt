package org.youtopia.server.utils

import org.youtopia.server.api.Server.game
import org.youtopia.data.Game
import org.youtopia.data.GameEffect
import org.youtopia.data.Player
import org.youtopia.data.Position
import org.youtopia.server.gameeffect.applyGameEffect
import kotlin.experimental.ExperimentalTypeInference
import kotlin.random.Random

internal fun neighbours(position: Position, size: Pair<Int, Int>): List<Position> = buildList {
    val notFirstColumn = position % size.first > 0
    val notLastColumn = position % size.first != size.first - 1
    val notFirstRow = position / size.first != 0
    val notLastRow = position / size.first != size.second - 1
    if (notFirstColumn) add(position - 1)
    if (notLastColumn) add(position + 1)
    if (notFirstRow) add(position - size.first)
    if (notLastRow) add(position + size.first)
    if (notFirstRow && notFirstColumn) add(position - size.first - 1)
    if (notFirstRow && notLastColumn) add(position - size.first + 1)
    if (notLastRow && notFirstColumn) add(position + size.first - 1)
    if (notLastRow && notLastColumn) add(position + size.first + 1)
}

internal fun Game.currentPlayer(): Player = players[currentPlayerIndex]

internal fun Game.nextPlayer(): Player = players[(currentPlayerIndex + 1) % players.size]

internal fun <T> List<T>.randomWeighted(vararg weights: Float): T {
    val sum = weights.sum()
    var random = Random.nextFloat() * sum
    weights.forEachIndexed { i, weight ->
        random -= weight
        if (random <= 0) return this[i]
    }
    return this[0]
}

internal fun <T> List<T>.modifyAt(indices: Iterable<Int>, block: T.() -> T) = toMutableList().apply {
    for (index in indices) {
        this[index] = this[index].block()
    }
}.toList()

@OptIn(ExperimentalTypeInference::class)
inline fun buildGameEffectsList(@BuilderInference builderAction: MutableList<GameEffect>.() -> Unit): List<GameEffect> =
    GameEffectList().apply(builderAction).toList()

class GameEffectList : ArrayList<GameEffect>() {
    override fun add(element: GameEffect): Boolean {
        game = game.update(applyGameEffect(element))
        return super.add(element)
    }
}
