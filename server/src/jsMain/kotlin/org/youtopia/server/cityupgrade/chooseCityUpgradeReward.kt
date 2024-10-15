package org.youtopia.server.cityupgrade

import org.youtopia.server.api.Server.cityUpgradeRequestQueue
import org.youtopia.server.api.Server.game
import org.youtopia.data.Building
import org.youtopia.data.CityUpgradeReward
import org.youtopia.data.CityUpgradeRewardRequest
import org.youtopia.data.GameEffect
import org.youtopia.data.Position
import org.youtopia.data.diff.ActionResult
import org.youtopia.server.mapgen.round
import org.youtopia.server.tileaction.addPopulation
import org.youtopia.server.utils.buildGameEffectsList

fun chooseCityUpgradeRewardImpl(
    pos: Position,
    reward: CityUpgradeReward,
): ActionResult {
    val cityUpgradeRewardRequests = mutableListOf<CityUpgradeRewardRequest>()
    return ActionResult(buildGameEffectsList {
        val populationCache = mutableMapOf<Position, Int>()
        val currentRequest = cityUpgradeRequestQueue.removeFirst()
        if (pos != currentRequest.pos || reward !in currentRequest.rewards) error("City upgrade request not found.")
        when (reward) {
            CityUpgradeReward.WORKSHOP -> add(
                GameEffect.AddWorkshop(
                    pos,
                    (game.tiles[pos].building as Building.City).copy(hasWorkshop = true)
                )
            )

            CityUpgradeReward.EXPLORER -> Unit // TODO
            CityUpgradeReward.CITY_WALL -> add(GameEffect.AddWall(pos, (game.tiles[pos].building as Building.City).copy(hasWalls = true)))
            CityUpgradeReward.RESOURCES -> add(GameEffect.UpdateStars(5))
            CityUpgradeReward.POPULATION_GROWTH -> repeat(3) { addPopulation(pos, populationCache, cityUpgradeRewardRequests) }
            CityUpgradeReward.BORDER_GROWTH -> add(
                GameEffect.UpdateTerritory(
                    city = pos,
                    territory = round(pos, 2, game.size).filter {
                        val city = game.tiles[it].city
                        return@filter city == pos || city == null
                    }.toSet()
                )
            )

            CityUpgradeReward.PARK -> {
                val city = game.tiles[pos].building as Building.City
                add(GameEffect.AddPark(pos, city.copy(parksCount = city.parksCount + 1)))
            }

            CityUpgradeReward.SUPER_UNIT -> Unit // TODO
        }
    }, cityUpgradeRewardRequests = cityUpgradeRewardRequests)//.onEach { game = game.update(applyGameEffect(it)) }
}
