package org.youtopia.server.api

import app.cash.zipline.ZiplineService
import org.youtopia.data.CityUpgradeReward
import org.youtopia.data.CityUpgradeRewardRequest
import org.youtopia.data.Game
import org.youtopia.data.GameEffect
import org.youtopia.data.GeneralAction
import org.youtopia.data.Position
import org.youtopia.data.RoadNetwork
import org.youtopia.data.TileAction
import org.youtopia.data.TroopAction
import org.youtopia.data.diff.ActionResult
import org.youtopia.data.diff.GameDiff

@Suppress("TooManyFunctions")
interface Commands : ZiplineService {
    fun requestAvailableDestinations(
        tilePosition: Position,
    ): Set<Position>

    fun requestAvailableTargets(
        tilePosition: Position,
    ): Set<Position>

    fun requestAvailableTroopActions(
        tilePosition: Position,
    ): List<TroopAction>

    fun requestAvailableTileActions(
        tilePosition: Position,
        tileActions: List<TileAction>,
    ): List<TileAction>

    fun performTroopAction(
        action: TroopAction,
    ): ActionResult

    fun performTileAction(
        pos: Position,
        action: TileAction,
    ): ActionResult

    fun moveTo(
        startPosition: Position,
        finishPosition: Position,
    ): ActionResult

    fun attack(
        attackerPosition: Position,
        targetPosition: Position,
    ): ActionResult

    fun performGeneralAction(
        action: GeneralAction,
    ): ActionResult

    fun chooseCityUpgradeReward(
        pos: Position,
        reward: CityUpgradeReward,
    ): ActionResult

    fun generateGame(size: Pair<Int, Int>): Pair<Game, Set<RoadNetwork>>

    fun syncGame(game: Game)

    fun syncGameDiff(gameDiff: GameDiff)

    fun calculateGameDiff(gameEffect: GameEffect): GameDiff

    fun syncRoadNetworks(roadNetworks: Set<RoadNetwork>)

    fun removeFirstCityUpgradeRequest()

    fun addCityUpgradeRequest(request: CityUpgradeRewardRequest)
}
