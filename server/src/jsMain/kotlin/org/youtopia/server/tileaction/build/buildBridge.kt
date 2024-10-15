package org.youtopia.server.tileaction.build

import org.youtopia.server.api.Server.game
import org.youtopia.server.api.Server.roadNetworks
import org.youtopia.data.Building
import org.youtopia.data.GameEffect
import org.youtopia.data.Position
import org.youtopia.data.RoadNetwork
import org.youtopia.data.Tile
import org.youtopia.server.utils.neighbours

fun MutableList<GameEffect>.buildBridge(
    tile: Tile,
    pos: Position,
    rotated: Boolean,
    addPopulation: MutableList<GameEffect>.(cell: Position) -> Unit,
) {
    add(GameEffect.UpdateStars(-5))
    if (tile.resource != null) {
        add(GameEffect.RemoveResource(pos))
    }
    add(GameEffect.Build(pos, Building.Bridge(rotated)))
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
                .fold(emptySet<Position>()) { acc, roadNetwork -> acc + roadNetwork.cities}
                .forEach {
                    addPopulation(it)
                    add(GameEffect.ConnectToCapital(it))
                    addPopulation(newNetworkCapital)
                }
        }
    }
}
