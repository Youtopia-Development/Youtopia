package org.youtopia.server.tileaction

import org.youtopia.server.api.Server.game
import org.youtopia.data.Building
import org.youtopia.data.Game
import org.youtopia.data.Position
import org.youtopia.data.Resource
import org.youtopia.data.Terrain
import org.youtopia.data.Tile
import org.youtopia.data.TileAction
import org.youtopia.data.TileActionAvailability
import org.youtopia.server.utils.currentPlayer
import org.youtopia.server.utils.neighbours

fun requestAvailableTileActionsImpl(
    tilePosition: Int,
): List<TileAction> = buildList {
    if (game.tiles[tilePosition].city == tilePosition) return@buildList

    val tile = game.tiles[tilePosition]

    addRoads(tile)

    addBridges(tile, tilePosition)

    if (!tile.ownedByCurrentPlayer(game)) return@buildList

    tile.building?.let {
        add(TileAction.DestroyBuilding(it, TileActionAvailability.AVAILABLE))
        return@buildList
    }

    addResourceCollection(tile)

    addTerrainSpecificActions(tile, tilePosition)
}

private fun MutableList<TileAction>.addRoads(tile: Tile) {
    if (
        !tile.road &&
        (tile.ownedByCurrentPlayer(game) || tile.owner == null) &&
        (tile.terrain == Terrain.FIELD || tile.terrain == Terrain.FOREST)
    ) {
        add(TileAction.BuildRoad(availability(price = 3, game)))
    }
}

private fun MutableList<TileAction>.addBridges(tile: Tile, tilePosition: Position) {
    if (
        tile.building == null &&
        (tile.ownedByCurrentPlayer(game) || tile.owner == null) &&
        tile.terrain == Terrain.WATER
    ) {
        if (
            tilePosition % game.size.first != 0 &&
            isNotWatery(tilePosition - 1, game) &&
            tilePosition % (game.size.first + 1) != 0 &&
            isNotWatery(tilePosition + 1, game)
        ) add(TileAction.Build(Building.Bridge(rotated = false), availability(price = 5, game)))
        else if (
            tilePosition >= game.size.first &&
            isNotWatery(tilePosition - game.size.first, game) &&
            tilePosition / game.size.first < game.size.second - 1 &&
            isNotWatery(tilePosition + game.size.second, game)
        ) add(TileAction.Build(Building.Bridge(rotated = true), availability(price = 5, game)))
    }
}

private fun MutableList<TileAction>.addResourceCollection(tile: Tile) {
    if (tile.resource == Resource.Game) {
        add(TileAction.CollectResource(Resource.Game, tile.tribe, availability(price = 2, game)))
    }

    if (tile.resource == Resource.Fruit) {
        add(TileAction.CollectResource(Resource.Fruit, tile.tribe, availability(price = 2, game)))
    }

    if (tile.resource == Resource.Fish) {
        add(TileAction.CollectResource(Resource.Fish, tile.tribe, availability(price = 2, game)))
    }
}

private fun MutableList<TileAction>.addTerrainSpecificActions(tile: Tile, tilePosition: Position) {
    when (tile.terrain) {
        Terrain.FOREST -> {
            add(TileAction.Build(Building.LumberHut, availability(price = 3, game)))
            add(TileAction.Build(Building.ForestTemple(level = 1, turnsToLevelUp = 3), availability(price = 15, game)))
            add(TileAction.ClearForest(TileActionAvailability.AVAILABLE))
            add(TileAction.BurnForest(availability(price = 5, game)))
        }

        Terrain.MOUNTAIN -> {
            add(TileAction.Build(Building.MountainTemple(level = 1, turnsToLevelUp = 3), availability(price = 20, game)))
            if (tile.resource == Resource.Metal)
                add(TileAction.Build(Building.Mine, availability(price = 5, game)))
        }

        Terrain.OCEAN -> {
            add(TileAction.Build(Building.WaterTemple(level = 1, turnsToLevelUp = 3), availability(price = 20, game)))
        }

        Terrain.WATER -> {
            add(TileAction.Build(Building.Port(emptySet(), emptySet()), availability(price = 7, game)))
            add(TileAction.Build(Building.WaterTemple(level = 1, turnsToLevelUp = 3), availability(price = 20, game)))
            // TODO: addMonuments(game)
        }

        Terrain.FIELD -> addFieldSpecificActions(tile, tilePosition)
    }
}

private fun MutableList<TileAction>.addFieldSpecificActions(tile: Tile, tilePosition: Position) {
    val city = game.currentPlayer().cities[game.tiles[tilePosition].city]

    add(TileAction.GrowForest(availability(price = 5, game)))

    val sawmillLevel = neighbours(tilePosition, game.size).count {
        game.tiles[it].building is Building.LumberHut && game.tiles[it].ownedByCurrentPlayer(game)
    }
    val windmillLevel = neighbours(tilePosition, game.size).count {
        game.tiles[it].building is Building.Farm && game.tiles[it].ownedByCurrentPlayer(game)
    }
    val forgeLevel = neighbours(tilePosition, game.size).count {
        game.tiles[it].building is Building.Mine && game.tiles[it].ownedByCurrentPlayer(game)
    }
    val marketLevel = neighbours(tilePosition, game.size).count {
        (game.tiles[it].building is Building.Sawmill ||
            game.tiles[it].building is Building.Windmill ||
            game.tiles[it].building is Building.Forge) &&
            game.tiles[it].ownedByCurrentPlayer(game)
    }

    if (city?.isSawmillBuilt != true && sawmillLevel > 0) {
        add(TileAction.Build(Building.Sawmill(sawmillLevel), availability(price = 5, game)))
    }
    if (city?.isWindmillBuilt != true && windmillLevel > 0) {
        add(TileAction.Build(Building.Windmill(windmillLevel), availability(price = 5, game)))
    }
    if (city?.isForgeBuilt != true && forgeLevel > 0) {
        add(TileAction.Build(Building.Forge(forgeLevel), availability(price = 5, game)))
    }
    if (city?.isMarketBuilt != true && marketLevel > 0) {
        add(TileAction.Build(Building.Market(
            marketLevel,
            nearSawmill = false,
            nearWindmill = false,
            nearForge = false,
        ), availability(price = 5, game)
        ))
    }

    if (tile.resource == Resource.Crop) {
        add(TileAction.Build(Building.Farm, availability(price = 5, game)))
    }

    add(TileAction.Build(Building.FieldTemple(level = 1, turnsToLevelUp = 2), availability(price = 20, game)))

    // TODO: addMonuments(game)
}

private fun isEnoughResources(price: Int, game: Game) =
    game.currentPlayer().stars >= price

private fun availability(price: Int, game: Game) = if (isEnoughResources(price, game))
    TileActionAvailability.AVAILABLE
else
    TileActionAvailability.NOT_ENOUGH_RESOURCES

private fun isNotWatery(cell: Int, game: Game) =
    game.tiles[cell].terrain.let { it != Terrain.WATER && it != Terrain.OCEAN }

private fun Tile.ownedByCurrentPlayer(game: Game): Boolean = owner == game.currentPlayer().id
