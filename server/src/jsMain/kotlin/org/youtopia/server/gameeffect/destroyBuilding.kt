package org.youtopia.server.gameeffect

import org.youtopia.data.Building
import org.youtopia.data.Game
import org.youtopia.data.GameEffect
import org.youtopia.data.diff.CityDiff
import org.youtopia.data.diff.GameDiff
import org.youtopia.data.diff.PlayerDiff
import org.youtopia.data.diff.TileDiff

fun destroyBuilding(
    action: GameEffect.DestroyBuilding,
    game: Game,
): GameDiff {
    val city = game.tiles[action.cell].city ?: error("Building at ${action.cell} does not belong to a city.")
    return when (game.tiles[action.cell].building) {
        is Building.Bridge -> {
            GameDiff(tilesDiff = mapOf(action.cell to TileDiff(removeBuilding = true, road = false)))
        }

        is Building.Port -> {
            val port = game.tiles[action.cell].building as Building.Port
            GameDiff(tilesDiff = mapOf(action.cell to TileDiff(removeBuilding = true, road = false)) +
                port.connectedPorts.associateWith { connectedPortCell ->
                    val connectedPort = game.tiles[connectedPortCell].building as Building.Port
                    TileDiff(building = connectedPort.copy(
                        connectedPorts = connectedPort.connectedPorts - action.cell,
                        waterRoutes = connectedPort.waterRoutes - setOf(
                            port.waterRoutes.first { it.first() == connectedPortCell || it.last() == connectedPortCell }
                        ),
                    ))
                }
            )
        }

        is Building.Sawmill -> {
            GameDiff(
                tilesDiff = mapOf(action.cell to TileDiff(removeBuilding = true)),
                playersDiff = mapOf(game.currentPlayerIndex to PlayerDiff(
                    citiesModified = mapOf(city to CityDiff(isSawmillBuilt = false)),
                )),
            )
        }

        is Building.Windmill -> {
            GameDiff(
                tilesDiff = mapOf(action.cell to TileDiff(removeBuilding = true)),
                playersDiff = mapOf(game.currentPlayerIndex to PlayerDiff(
                    citiesModified = mapOf(city to CityDiff(isWindmillBuilt = false)),
                )),
            )
        }

        is Building.Forge -> {
            GameDiff(
                tilesDiff = mapOf(action.cell to TileDiff(removeBuilding = true)),
                playersDiff = mapOf(game.currentPlayerIndex to PlayerDiff(
                    citiesModified = mapOf(city to CityDiff(isForgeBuilt = false)),
                )),
            )
        }

        is Building.Market -> {
            GameDiff(
                tilesDiff = mapOf(action.cell to TileDiff(removeBuilding = true)),
                playersDiff = mapOf(game.currentPlayerIndex to PlayerDiff(
                    citiesModified = mapOf(city to CityDiff(isMarketBuilt = false)),
                )),
            )
        }

        else -> {
            GameDiff(tilesDiff = mapOf(action.cell to TileDiff(removeBuilding = true)))
        }
    }
}
