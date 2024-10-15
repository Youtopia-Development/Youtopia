package org.youtopia.server.utils

import org.youtopia.data.Building
import org.youtopia.data.Position
import org.youtopia.data.RoadNetwork
import org.youtopia.data.Terrain
import org.youtopia.data.Tile

internal fun dfsNewRoadNetwork(
    removedCell: Position,
    root: Position,
    tiles: List<Tile>,
    gameSize: Pair<Int, Int>,
    roads: MutableSet<Position> = mutableSetOf(),
    cities: MutableSet<Position> = mutableSetOf(),
    capital: Position? = null,
): RoadNetwork {
    var newCapital: Position? = null
    roads.add(root)
    if (tiles[root].building is Building.City) {
        cities.add(root)
        if ((tiles[root].building as Building.City).isCapital) newCapital = root
    }
    var network = RoadNetwork(roads, cities, newCapital ?: capital)
    for (neighbour in neighbours(root, gameSize) +
        if (tiles[root].building is Building.Port) (tiles[root].building as Building.Port).connectedPorts else emptySet()
    ) {
        if (neighbour !in roads &&
            neighbour != removedCell &&
            (tiles[neighbour].road || tiles[neighbour].building is Building.Bridge || tiles[neighbour].building is Building.Port)
        ) network = RoadNetwork.merge(network, dfsNewRoadNetwork(
            removedCell,
            neighbour,
            tiles,
            gameSize,
            roads,
            cities,
            newCapital ?: capital,
        )
        )
    }
    return network
}

internal fun bfsPortConnectionPaths(
    root: Position,
    tiles: List<Tile>,
    gameSize: Pair<Int, Int>,
): Set<List<Position>> {
    val connections = mutableSetOf<Node>()
    val visited = mutableSetOf<Position>()
    val queue = ArrayDeque<Pair<Node, Int>>()
    queue.add(Node(root, null) to 0)
    while (queue.isNotEmpty()) {
        val (node, rank) = queue.removeFirst()
        if (node.cell in visited) continue
        visited.add(node.cell)
        for (neighbour in neighbours(node.cell, gameSize)) {
            if (neighbour in visited || (tiles[neighbour].terrain != Terrain.WATER && tiles[neighbour].terrain != Terrain.OCEAN)) continue
            if (rank < 3) queue.add(Node(neighbour, node) to rank + 1)
            if (rank >= 0 &&
                tiles[neighbour].building is Building.Port &&
                !connections.any { it.cell == neighbour }
            ) connections.add(Node(neighbour, node))
        }
    }
    return connections.map(Node::toList).toSet()
}

private data class Node(
    val cell: Position,
    val parent: Node?,
) {
    fun toList(): List<Position> = buildList {
        var node: Node? = this@Node
        while (node != null) {
            add(node.cell)
            node = node.parent
        }
    }
}
