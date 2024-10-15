package org.youtopia.server.gameeffect

import org.youtopia.data.Building
import org.youtopia.data.Game
import org.youtopia.data.GameEffect
import org.youtopia.data.diff.CityDiff
import org.youtopia.data.diff.GameDiff
import org.youtopia.data.diff.PlayerDiff
import org.youtopia.data.diff.TileDiff
import org.youtopia.server.utils.currentPlayer

fun upgradeCity(
    action: GameEffect.UpgradeCity,
    game: Game,
): GameDiff {
    val city = game.currentPlayer().cities[action.cell] ?: error("City not found at the location ${action.cell}")
    val cityBuilding = game.tiles[action.cell].building as Building.City
    return GameDiff(
        playersDiff = mapOf(
            game.currentPlayerIndex to PlayerDiff(
                citiesModified = mapOf(
                    action.cell to CityDiff(
                        level = city.level + 1,
                        population = 0,
                        targetPopulation = city.targetPopulation + 1,
                        incomeDiff = 1,
                    )
                ),
                incomeDiff = 1,
                scoreDiff = action.score,
            )
        ),
        tilesDiff = mapOf(action.cell to TileDiff(building = cityBuilding.copy(level = cityBuilding.level + 1))),
    )
}
