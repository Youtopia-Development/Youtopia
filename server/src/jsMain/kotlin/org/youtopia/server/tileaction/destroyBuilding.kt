package org.youtopia.server.tileaction

import org.youtopia.server.api.Server.game
import org.youtopia.server.api.Server.roadNetworks
import org.youtopia.data.Building
import org.youtopia.data.GameEffect
import org.youtopia.data.PlayerId
import org.youtopia.data.Position
import org.youtopia.data.RoadNetwork
import org.youtopia.data.Tile
import org.youtopia.data.diff.ActionResult
import org.youtopia.server.utils.buildGameEffectsList
import org.youtopia.server.utils.dfsNewRoadNetwork
import org.youtopia.server.utils.neighbours

internal fun destroyBuilding(
    pos: Int,
    tile: Tile,
    tileOwner: PlayerId?,
): ActionResult {
    return ActionResult(buildGameEffectsList {
        val building = tile.building ?: error("Building not found at cell $pos.")
        val city = game.tiles[pos].city ?: error("Building doesn't belong to a city.")
        add(GameEffect.DestroyBuilding(pos, tile.resource, tile.tribe))

        handleBuilding(building, city)
        handleNeighbours(building, pos, tileOwner)
        handleConnections(building, pos)
    }, roadNetworks)
}

private fun MutableList<GameEffect>.handleBuilding(
    building: Building,
    city: Int,
) {
    when (building) {
        is Building.LumberHut,
        is Building.Port,
        is Building.FieldTemple,
        is Building.MountainTemple,
        is Building.ForestTemple,
        is Building.WaterTemple,
        -> add(GameEffect.RemovePopulation(city))

        is Building.Farm,
        is Building.Mine,
        -> {
            add(GameEffect.RemovePopulation(city))
            add(GameEffect.RemovePopulation(city))
        }

        is Building.Sawmill -> repeat(building.level) {
            add(GameEffect.RemovePopulation(city))
        }

        is Building.Windmill -> repeat(building.level) {
            add(GameEffect.RemovePopulation(city))
        }

        is Building.Forge -> repeat(building.level) {
            add(GameEffect.RemovePopulation(city))
            add(GameEffect.RemovePopulation(city))
        }

        is Building.Monument -> {
            add(GameEffect.RemovePopulation(city))
            add(GameEffect.RemovePopulation(city))
            add(GameEffect.RemovePopulation(city))
        }

        is Building.Market -> {
            add(GameEffect.UpdateIncome(-building.level))
        }

        else -> Unit
    }
}

private fun MutableList<GameEffect>.handleNeighbours(
    building: Building,
    cell: Position,
    tileOwner: PlayerId?,
) {
    for (neighbourPos in neighbours(cell, game.size)) {
        if (game.tiles[neighbourPos].owner != tileOwner) continue
        game.tiles[neighbourPos].building?.let { neighbour ->
            handleNeighbour(building, neighbour, cell, neighbourPos, tileOwner)
            handleMarketChanges(building, neighbour, cell, neighbourPos, tileOwner)
        }
    }
}

private fun MutableList<GameEffect>.handleNeighbour(
    building: Building,
    neighbour: Building,
    cell: Position,
    neighbourPos: Position,
    tileOwner: PlayerId?,
) {
    if (!isBuildingAffected(building, neighbour)) return
    neighbour as Building.UpgradeableBuilding<*>
    add(GameEffect.DowngradeBuilding(neighbourPos, neighbour.downgrade()))
    add(GameEffect.RemovePopulation(game.tiles[neighbourPos].city ?: error("City not found at cell $cell")))
    for (nPos in neighbours(neighbourPos, game.size)) {
        if (game.tiles[nPos].owner != tileOwner) continue
        game.tiles[nPos].building?.let { neighbourBuilding ->
            if (neighbourBuilding is Building.Market) {
                add(GameEffect.DowngradeBuilding(nPos, neighbourBuilding.downgrade()))
                add(GameEffect.UpdateIncome(-1))
            }
        }
    }
}

private fun MutableList<GameEffect>.handleMarketChanges(
    building: Building,
    neighbour: Building,
    cell: Position,
    neighbourPos: Position,
    tileOwner: PlayerId?,
) {
    if ((building !is Building.Sawmill &&
            building !is Building.Windmill &&
            building !is Building.Forge) || neighbour !is Building.Market) return

    var nearSawmill = false
    var nearWindmill = false
    var nearForge = false
    for (marketNeighbour in neighbours(neighbourPos, game.size) - cell) {
        if (game.tiles[marketNeighbour].owner != tileOwner) continue
        when (game.tiles[marketNeighbour].building) {
            is Building.Sawmill -> {
                nearSawmill = true
            }

            is Building.Windmill -> {
                nearWindmill = true
            }

            is Building.Forge -> {
                nearForge = true
            }

            else -> Unit
        }
    }
    repeat((building as? Building.UpgradeableBuilding<*>)?.level ?: 1) {
        add(
            GameEffect.DowngradeBuilding(
                neighbourPos,
                neighbour.downgrade().setStands(nearSawmill, nearWindmill, nearForge),
            )
        )
        add(GameEffect.UpdateIncome(-1))
    }
}

@Suppress("NestedBlockDepth")
private fun MutableList<GameEffect>.handleConnections(
    building: Building,
    cell: Position,
) {
    if (building !is Building.Port && building !is Building.Bridge) return
    val roadNetwork = roadNetworks.first { cell in it.roads }
    val newNetworks = mutableSetOf<RoadNetwork>()
    val neighbours = neighbours(cell, game.size).filter { game.tiles[it].road }.toSet()
    val removedNeighbours = mutableSetOf<Position>()
    val removedPorts = mutableSetOf<Position>()
    val capitalNetwork: Ref<RoadNetwork?> = Ref(null)
    for (neighbour in neighbours) {
        if (neighbour in removedNeighbours) continue
        val newNetwork = dfsNewRoadNetwork(cell, neighbour, game.tiles, game.size)
        for (roadNeighbour in neighbours) {
            if (roadNeighbour in newNetwork.roads) removedNeighbours.add(roadNeighbour)
        }
        if (building is Building.Port) {
            for (connectedPort in building.connectedPorts) {
                if (connectedPort in newNetwork.roads) removedPorts.add(connectedPort)
            }
        }
        if (newNetwork.capital != null) capitalNetwork.value = newNetwork
        newNetworks.add(newNetwork)
    }
    eraseWaterRoutes(building, cell, removedPorts, capitalNetwork, newNetworks)
    roadNetworks.remove(roadNetwork)
    roadNetworks.addAll(newNetworks)
    capitalNetwork.value?.let { capitalNetworkValue ->
        for (networkCity in roadNetwork.cities) {
            if (networkCity !in capitalNetworkValue.cities) {
                add(GameEffect.RemovePopulation(networkCity))
                add(GameEffect.DisconnectFromCapital(networkCity))
                add(GameEffect.RemovePopulation(capitalNetworkValue.capital ?: error("Capital network does not have a capital.")))
            }
        }
    }
}

private fun MutableList<GameEffect>.eraseWaterRoutes(
    building: Building,
    cell: Position,
    removedPorts: MutableSet<Position>,
    capitalNetwork: Ref<RoadNetwork?>,
    newNetworks: MutableSet<RoadNetwork>,
) {
    if (building !is Building.Port) return
    for (connectedPort in building.connectedPorts) {
        if (connectedPort in removedPorts) continue
        val newNetwork = dfsNewRoadNetwork(cell, connectedPort, game.tiles, game.size)
        for (cPort in building.connectedPorts) {
            if (cPort in newNetwork.roads) removedPorts.add(cPort)
        }
        if (newNetwork.capital != null) capitalNetwork.value = newNetwork
        newNetworks.add(newNetwork)
    }
    add(GameEffect.EraseWaterRoutes(building.waterRoutes))
    building.connectedPorts.forEach {
        add(GameEffect.ChartWaterRoutes((game.tiles[it].building as Building.Port).waterRoutes.filterNot { route ->
            route.first() == cell || route.last() == cell
        }.toSet()))
    }
}

private fun isBuildingAffected(from: Building, to: Building) =
    (from is Building.LumberHut && to is Building.Sawmill) ||
        (from is Building.Farm && to is Building.Windmill) ||
        (from is Building.Mine && to is Building.Forge)

class Ref<T>(var value: T)
