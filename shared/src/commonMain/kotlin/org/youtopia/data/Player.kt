package org.youtopia.data

import kotlinx.serialization.Serializable
import org.youtopia.data.diff.PlayerDiff

@Serializable
data class Player(
    val id: Int,
    val name: String,
    val tribe: Tribe,
    val color: Int,
    val score: Int,
    val stars: Int,
    val income: Int,
    val fog: Set<Position>,
    val cities: Map<Position, City>,
    val troops: Map<Position, Troop> = emptyMap(),
    val taskProgress: Map<Task, Int> = emptyMap(),
) {
    fun update(diff: PlayerDiff): Player = Player(
        id = diff.id ?: id,
        name = diff.name ?: name,
        tribe = diff.tribe ?: tribe,
        color = diff.color ?: color,
        score = score + (diff.scoreDiff ?: 0),
        stars = stars + (diff.starsDiff ?: 0),
        income = income + (diff.incomeDiff ?: 0),
        fog = (fog - diff.fog) + (diff.fog - fog),
        cities = (if (diff.citiesModified.isNotEmpty()) cities.toMutableMap().apply {
            diff.citiesModified.forEach { this[it.key]?.let { old -> put(it.key, old.update(it.value)) }}
        } else cities + diff.citiesNew).toMap(),
        troops = (if (diff.troopsModified.isNotEmpty()) troops.toMutableMap().apply {
            diff.troopsModified.forEach { this[it.key]?.let { old -> put(it.key, old.update(it.value)) }}
        } else troops + diff.troopsNew).toMap(),
        taskProgress = taskProgress + diff.taskProgress,
    )
}
