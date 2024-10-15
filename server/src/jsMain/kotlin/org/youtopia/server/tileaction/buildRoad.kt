package org.youtopia.server.tileaction

import org.youtopia.server.api.Server.game
import org.youtopia.server.api.Server.roadNetworks
import org.youtopia.data.CityUpgradeRewardRequest
import org.youtopia.data.GameEffect
import org.youtopia.data.Position
import org.youtopia.data.RoadNetwork
import org.youtopia.data.diff.ActionResult
import org.youtopia.server.utils.buildGameEffectsList
import org.youtopia.server.utils.neighbours

internal fun buildRoad(
    pos: Int,
    addPopulation: MutableList<GameEffect>.(cell: Position) -> Unit,
    cityUpgradeRewardRequests: MutableList<CityUpgradeRewardRequest>,
): ActionResult {
    return ActionResult(buildGameEffectsList {
        add(GameEffect.UpdateStars(-3))
        add(GameEffect.BuildRoad(pos))
        val affectedNetworks = mutableSetOf<RoadNetwork>()
        for (neighbour in neighbours(pos, game.size)) {
            affectedNetworks.addAll(roadNetworks.filter { neighbour in it.roads }.toSet())
        }
        if (affectedNetworks.isEmpty()) {
            roadNetworks.add(RoadNetwork(roads = setOf(pos), capital = null, cities = emptySet()))
        } else {
            roadNetworks.removeAll(affectedNetworks)
            val newNetwork = RoadNetwork.merge(affectedNetworks, pos)
            roadNetworks.add(newNetwork)
            newNetwork.capital?.let { newNetworkCapital ->
                affectedNetworks
                    .filter { it.capital == null }
                    .fold(emptySet<Position>()) { acc, roadNetwork -> acc + roadNetwork.cities }
                    .forEach {
                        addPopulation(it)
                        add(GameEffect.ConnectToCapital(it))
                        addPopulation(newNetworkCapital)
                    }
            }
        }
    }, roadNetworks, cityUpgradeRewardRequests)
}
