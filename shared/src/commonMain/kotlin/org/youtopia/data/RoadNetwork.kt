package org.youtopia.data

import kotlinx.serialization.Serializable

@Serializable
data class RoadNetwork(
    val roads: Set<Position>,
    val cities: Set<Position>,
    val capital: Position?,
) {
    companion object {
        fun merge(networks: Set<RoadNetwork>, newRoad: Position? = null): RoadNetwork = RoadNetwork(
            roads = networks.fold(newRoad?.let { setOf(it) } ?: emptySet()) { acc, roadNetwork -> acc + roadNetwork.roads },
            capital = networks.fold<RoadNetwork, Position?>(null) { acc, roadNetwork -> roadNetwork.capital ?: acc },
            cities = networks.fold(emptySet()) { acc, roadNetwork -> acc + roadNetwork.cities },
        )

        fun merge(vararg networks: RoadNetwork): RoadNetwork = RoadNetwork(
            roads = networks.fold(emptySet()) { acc, roadNetwork -> acc + roadNetwork.roads },
            capital = networks.fold<RoadNetwork, Position?>(null) { acc, roadNetwork -> roadNetwork.capital ?: acc },
            cities = networks.fold(emptySet()) { acc, roadNetwork -> acc + roadNetwork.cities },
        )
    }
}
