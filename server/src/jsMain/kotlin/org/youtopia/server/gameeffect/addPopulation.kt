package org.youtopia.server.gameeffect

import org.youtopia.data.Game
import org.youtopia.data.GameEffect
import org.youtopia.data.diff.CityDiff
import org.youtopia.data.diff.GameDiff
import org.youtopia.data.diff.PlayerDiff
import org.youtopia.server.utils.currentPlayer

fun addPopulation(
    action: GameEffect.AddPopulation,
    game: Game,
): GameDiff {
    val city = game.currentPlayer().cities[action.to] ?: error("City not found at the location ${action.to}")
    return GameDiff(
        playersDiff = mapOf(
            game.currentPlayerIndex to PlayerDiff(
                citiesModified = mapOf(
                    action.to to CityDiff(
                        population = city.population + 1,
                        incomeDiff = if (city.population < 0) 1 else 0,
                    )
                ),
                incomeDiff = if (city.population < 0) 1 else 0,
                scoreDiff = 5,
            )
        )
    )
}
