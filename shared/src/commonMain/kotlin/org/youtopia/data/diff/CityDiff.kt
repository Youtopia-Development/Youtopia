package org.youtopia.data.diff

import kotlinx.serialization.Serializable
import org.youtopia.data.Position

@Serializable
data class CityDiff(
    val name: String? = null,
    val territory: Set<Position>? = null,
    val score: Int? = null,
    val level: Int? = null,
    val population: Int? = null,
    val targetPopulation: Int? = null,
    val unitCount: Int? = null,
    val maxUnitCount: Int? = null,
    val isCapital: Boolean? = null,
    val incomeDiff: Int? = null,
    val isSawmillBuilt: Boolean? = null,
    val isWindmillBuilt: Boolean? = null,
    val isForgeBuilt: Boolean? = null,
    val isMarketBuilt: Boolean? = null,
    val connectedToCapital: Boolean? = null,
)
