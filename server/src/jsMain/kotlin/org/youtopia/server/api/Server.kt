package org.youtopia.server.api

import org.youtopia.server.cityupgrade.chooseCityUpgradeRewardImpl
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
import org.youtopia.server.gameeffect.applyGameEffect
import org.youtopia.server.generalaction.performGeneralActionImpl
import org.youtopia.server.mapgen.generateGameImpl
import org.youtopia.server.tileaction.performTileActionImpl
import org.youtopia.server.tileaction.requestAvailableTileActionsImpl

@Suppress("TooManyFunctions")
object Server : Commands {

    var game: Game = Game(tiles = emptyList(), size = 0 to 0, players = emptyList(), turn = 0, currentPlayerIndex = 0)
    val roadNetworks: MutableSet<RoadNetwork> = mutableSetOf()
    val cityUpgradeRequestQueue: ArrayDeque<CityUpgradeRewardRequest> = ArrayDeque()

    override fun requestAvailableDestinations(
        tilePosition: Int,
    ): Set<Int> {
        TODO("Not yet implemented")
    }

    override fun requestAvailableTargets(
        tilePosition: Int,
    ): Set<Int> {
        TODO("Not yet implemented")
    }

    override fun requestAvailableTroopActions(
        tilePosition: Int,
    ): List<TroopAction> {
        TODO("Not yet implemented")
    }

    override fun requestAvailableTileActions(
        tilePosition: Int,
        tileActions: List<TileAction>,
    ): List<TileAction> = requestAvailableTileActionsImpl(tilePosition)

    override fun performTroopAction(
        action: TroopAction,
    ): ActionResult {
        TODO("Not yet implemented")
    }

    override fun performTileAction(
        pos: Position,
        action: TileAction,
    ): ActionResult = performTileActionImpl(pos, action)

    override fun moveTo(
        startPosition: Int,
        finishPosition: Int,
    ): ActionResult {
        TODO("Not yet implemented")
    }

    override fun attack(
        attackerPosition: Int,
        targetPosition: Int,
    ): ActionResult {
        TODO("Not yet implemented")
    }

    override fun performGeneralAction(action: GeneralAction): ActionResult = performGeneralActionImpl(action)

    override fun chooseCityUpgradeReward(
        pos: Position,
        reward: CityUpgradeReward,
    ): ActionResult = chooseCityUpgradeRewardImpl(pos, reward)

    override fun generateGame(size: Pair<Int, Int>): Pair<Game, Set<RoadNetwork>> = generateGameImpl(size).also {
        syncGame(it.first)
        syncRoadNetworks(it.second)
    }

    override fun syncGame(game: Game) {
        this.game = game
    }

    override fun syncGameDiff(gameDiff: GameDiff) {
        game = game.update(gameDiff)
    }

    override fun calculateGameDiff(gameEffect: GameEffect): GameDiff = applyGameEffect(gameEffect)

    override fun syncRoadNetworks(roadNetworks: Set<RoadNetwork>) {
        this.roadNetworks.clear()
        this.roadNetworks.addAll(roadNetworks)
    }

    override fun removeFirstCityUpgradeRequest() {
        cityUpgradeRequestQueue.removeFirst()
    }

    override fun addCityUpgradeRequest(request: CityUpgradeRewardRequest) {
        cityUpgradeRequestQueue.add(request)
    }
}
