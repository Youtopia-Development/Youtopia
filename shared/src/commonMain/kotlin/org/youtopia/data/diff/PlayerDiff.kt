package org.youtopia.data.diff

import kotlinx.serialization.Serializable
import org.youtopia.data.City
import org.youtopia.data.Position
import org.youtopia.data.Task
import org.youtopia.data.Tribe
import org.youtopia.data.Troop

@Serializable
data class PlayerDiff(
    val id: Int? = null,
    val name: String? = null,
    val tribe: Tribe? = null,
    val color: Int? = null,
    val scoreDiff: Int? = null,
    val starsDiff: Int? = null,
    val incomeDiff: Int? = null,
    val fog: Set<Position> = emptySet(),
    val citiesModified: Map<Position, CityDiff> = emptyMap(),
    val citiesNew: Map<Position, City> = emptyMap(),
    val troopsModified: Map<Position, TroopDiff> = emptyMap(),
    val troopsNew: Map<Position, Troop> = emptyMap(),
    val taskProgress: Map<Task, Int> = emptyMap(),
)
