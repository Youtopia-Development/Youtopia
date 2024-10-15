package org.youtopia.server.gameeffect

import org.youtopia.data.Building
import org.youtopia.data.Game
import org.youtopia.data.GameEffect
import org.youtopia.data.diff.CityDiff
import org.youtopia.data.diff.GameDiff
import org.youtopia.data.diff.PlayerDiff
import org.youtopia.data.diff.TileDiff

fun build(
    action: GameEffect.Build,
    game: Game,
): GameDiff {
    if (action.building is Building.Bridge) {
        return GameDiff(
            tilesDiff = mapOf(action.cell to TileDiff(building = action.building, road = true)),
        )
    }
    val city = game.tiles[action.cell].city ?: error("Building at ${action.cell} does not belong to a city.")
    return when (action.building) {
        is Building.Sawmill -> GameDiff(
            tilesDiff = mapOf(action.cell to TileDiff(building = action.building)),
            playersDiff = mapOf(
                game.currentPlayerIndex to PlayerDiff(
                    citiesModified = mapOf(city to CityDiff(isSawmillBuilt = true))
                )
            )
        )

        is Building.Windmill -> GameDiff(
            tilesDiff = mapOf(action.cell to TileDiff(building = action.building)),
            playersDiff = mapOf(
                game.currentPlayerIndex to PlayerDiff(
                    citiesModified = mapOf(city to CityDiff(isWindmillBuilt = true))
                )
            )
        )

        is Building.Forge -> GameDiff(
            tilesDiff = mapOf(action.cell to TileDiff(building = action.building)),
            playersDiff = mapOf(
                game.currentPlayerIndex to PlayerDiff(
                    citiesModified = mapOf(city to CityDiff(isForgeBuilt = true))
                )
            )
        )

        is Building.Market -> GameDiff(
            tilesDiff = mapOf(action.cell to TileDiff(building = action.building)),
            playersDiff = mapOf(
                game.currentPlayerIndex to PlayerDiff(
                    citiesModified = mapOf(city to CityDiff(isMarketBuilt = true)),
                )
            )
        )

        is Building.Port -> GameDiff(
            tilesDiff = mapOf(action.cell to TileDiff(building = action.building, road = true)) +
                (action.building as Building.Port).connectedPorts.associateWith { connectedPortCell ->
                    val connectedPort = game.tiles[connectedPortCell].building as Building.Port
                    TileDiff(
                        building = connectedPort.copy(
                            connectedPorts = connectedPort.connectedPorts + action.cell,
                            waterRoutes = connectedPort.waterRoutes + setOf((action.building as Building.Port).waterRoutes.first {
                                it.first() == connectedPortCell
                            }),
                        )
                    )
                },
        )

        else -> GameDiff(tilesDiff = mapOf(action.cell to TileDiff(building = action.building)))
    }
}
