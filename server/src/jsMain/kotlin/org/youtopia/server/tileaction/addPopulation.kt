package org.youtopia.server.tileaction

import org.youtopia.server.api.Server.cityUpgradeRequestQueue
import org.youtopia.server.api.Server.game
import org.youtopia.data.Building
import org.youtopia.data.CityUpgradeRewardRequest
import org.youtopia.data.GameEffect
import org.youtopia.data.Position
import org.youtopia.data.upgradeRewardsForLevel
import org.youtopia.server.utils.currentPlayer

internal fun MutableList<GameEffect>.addPopulation(
    from: Position,
    populationCache: MutableMap<Position, Int>,
    cityUpgradeRewardRequests: MutableList<CityUpgradeRewardRequest>,
) {
    val cityPos = game.tiles[from].city ?: error("Tile does not belong to a city.")
    val city = game.currentPlayer().cities[cityPos] ?: error("City not found at the location $cityPos")
    val cityBuilding = game.tiles[cityPos].building as Building.City
    add(GameEffect.AddPopulation(from, cityPos))
    val population = populationCache[cityPos] ?: city.population
    if (population + 1 == city.targetPopulation) {
        val upgradeRewards = upgradeRewardsForLevel(cityBuilding.level + 1)
        add(GameEffect.UpgradeCity(
            cell = cityPos,
            city = cityBuilding.copy(level = cityBuilding.level + 1),
            targetPopulation = city.targetPopulation + 1,
            score = 50 - city.targetPopulation * 5,
            upgradeRewards = upgradeRewards,
        ))
        val request = CityUpgradeRewardRequest(cityPos, upgradeRewards)
        cityUpgradeRewardRequests.add(request)
        cityUpgradeRequestQueue.add(request)
        populationCache[cityPos] = 0
    } else {
        populationCache[cityPos] = population + 1
    }
}
