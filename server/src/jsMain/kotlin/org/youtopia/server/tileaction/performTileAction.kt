package org.youtopia.server.tileaction

import org.youtopia.server.api.Server.game
import org.youtopia.data.CityUpgradeRewardRequest
import org.youtopia.data.GameEffect
import org.youtopia.data.Position
import org.youtopia.data.TileAction
import org.youtopia.data.diff.ActionResult
import org.youtopia.server.tileaction.build.build

internal fun performTileActionImpl(
    pos: Position,
    action: TileAction,
): ActionResult {
    val tile = game.tiles[pos]
    val tileOwner = tile.owner
    val populationCache = mutableMapOf<Position, Int>()
    val cityUpgradeRewardRequests = mutableListOf<CityUpgradeRewardRequest>()

    fun MutableList<GameEffect>.addPopulation(cell: Position) = addPopulation(cell, populationCache, cityUpgradeRewardRequests)

    return when (action) {
        is TileAction.Build -> build(action, pos, tile, tileOwner, MutableList<GameEffect>::addPopulation, cityUpgradeRewardRequests)
        is TileAction.BuildRoad -> buildRoad(pos, MutableList<GameEffect>::addPopulation, cityUpgradeRewardRequests)
        is TileAction.BurnForest -> burnForest(pos, tile)
        is TileAction.ClearForest -> clearForest(pos, tile)
        is TileAction.CollectResource -> collectResource(action, pos, MutableList<GameEffect>::addPopulation, cityUpgradeRewardRequests)
        is TileAction.DestroyBuilding -> destroyBuilding(pos, tile, tileOwner)
        is TileAction.GrowForest -> growForest(pos, tile)
        is TileAction.Recruit -> TODO()
    }//.onEach { game = game.update(applyGameEffect(it)) }
}
