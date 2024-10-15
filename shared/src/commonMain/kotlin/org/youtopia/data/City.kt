package org.youtopia.data

import kotlinx.serialization.Serializable
import org.youtopia.data.diff.CityDiff

@Serializable
data class City(
    val name: String,
    val territory: Set<Position>,
    val score: Int = 100,
    val level: Int = 1,
    val population: Int = 0,
    val targetPopulation: Int = 2,
    val unitCount: Int = 0,
    val maxUnitCount: Int = 2,
    val isCapital: Boolean = false,
    val income: Int = if (isCapital) level + 1 else level,
    val isSawmillBuilt: Boolean = false,
    val isWindmillBuilt: Boolean = false,
    val isForgeBuilt: Boolean = false,
    val isMarketBuilt: Boolean = false,
    val connectedToCapital: Boolean = false,
) {
    fun update(diff: CityDiff): City = City(
        name = diff.name ?: name,
        territory = diff.territory ?: territory,
        score = diff.score ?: score,
        level = diff.level ?: level,
        population = diff.population ?: population,
        targetPopulation = diff.targetPopulation ?: targetPopulation,
        unitCount = diff.unitCount ?: unitCount,
        maxUnitCount = diff.maxUnitCount ?: maxUnitCount,
        isCapital = diff.isCapital ?: isCapital,
        income = income + (diff.incomeDiff ?: 0),
        isSawmillBuilt = diff.isSawmillBuilt ?: isSawmillBuilt,
        isWindmillBuilt = diff.isWindmillBuilt ?: isWindmillBuilt,
        isForgeBuilt = diff.isForgeBuilt ?: isForgeBuilt,
        isMarketBuilt = diff.isMarketBuilt ?: isMarketBuilt,
        connectedToCapital = diff.connectedToCapital ?: connectedToCapital,
    )
}
