package org.youtopia.server.zipline

import app.cash.zipline.ZiplineException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path.Companion.toPath
import org.youtopia.data.CityUpgradeReward
import org.youtopia.data.Game
import org.youtopia.data.GameEffect
import org.youtopia.data.GeneralAction
import org.youtopia.data.Position
import org.youtopia.data.TileAction

val serverBundles: MutableList<ServerBundle> = mutableListOf()

actual fun initialiseServerBundles(modsDirectory: String) {
    serverBundles.addAll(
        FileSystem.SYSTEM.list(modsDirectory.toPath()).filterNot { it.name.endsWith(".DS_Store") }.map {
            getServerBundle(it.toString())
        }
    )
    serverBundles.add(getServerBundle())
}

actual fun generateGame(size: Pair<Int, Int>): Game =
    runBlocking {
        val (actionResult, bundle) = invokeOnFirstMod { commands.generateGame(size) }
        val (game, roadNetworks) = actionResult
        serverBundles.filter { it !== bundle }.forEach {
            runBlocking(it.dispatcher) {
                it.commands.syncGame(game)
                it.commands.syncRoadNetworks(roadNetworks)
            }
        }
        game
    }

actual fun requestAvailableTileActions(tilePosition: Position): List<TileAction> =
    runBlocking {
        var tileActions = emptyList<TileAction>()
        serverBundles.reversed().forEach {
            withContext(it.dispatcher) {
                try {
                    tileActions = it.commands.requestAvailableTileActions(tilePosition, tileActions)
                } catch (e: ZiplineException) {
                    if (!e.toString().contains("NotImplementedError")) throw e
                }
            }
        }
        tileActions
    }

actual fun performTileAction(tilePosition: Position, action: TileAction): List<GameEffect> =
    runBlocking {
        val (actionResult, bundle) = invokeOnFirstMod { commands.performTileAction(tilePosition, action) }
        val gameDiffs = try {
            withContext(bundle.dispatcher) {
                actionResult.gameEffects.map {
                    bundle.commands.calculateGameDiff(it)
                }
            }
        } catch (e: ZiplineException) {
            if (!e.toString().contains("NotImplementedError")) throw e

            val defaultBundle = serverBundles.last()
            withContext(defaultBundle.dispatcher) {
                actionResult.gameEffects.map {
                    defaultBundle.commands.calculateGameDiff(it)
                }
            }
        }
        serverBundles.filter { it !== bundle }.map {
            async(it.dispatcher) {
                gameDiffs.forEach { gameDiff ->
                    it.commands.syncGameDiff(gameDiff)
                }
                actionResult.roadNetworks?.let { networks -> it.commands.syncRoadNetworks(networks) }
                actionResult.cityUpgradeRewardRequests?.let { requests ->
                    requests.forEach { request -> it.commands.addCityUpgradeRequest(request) }
                }
            }
        }.awaitAll()
        return@runBlocking actionResult.gameEffects
    }

actual fun performGeneralAction(action: GeneralAction): List<GameEffect> =
    runBlocking {
        val (actionResult, bundle) = invokeOnFirstMod { commands.performGeneralAction(action) }
        val gameDiffs = try {
            withContext(bundle.dispatcher) {
                actionResult.gameEffects.map {
                    bundle.commands.calculateGameDiff(it)
                }
            }
        } catch (e: ZiplineException) {
            if (!e.toString().contains("NotImplementedError")) throw e

            val defaultBundle = serverBundles.last()
            withContext(defaultBundle.dispatcher) {
                actionResult.gameEffects.map {
                    defaultBundle.commands.calculateGameDiff(it)
                }
            }
        }
        serverBundles.filter { it !== bundle }.map {
            async(it.dispatcher) {
                gameDiffs.forEach { gameDiff ->
                    it.commands.syncGameDiff(gameDiff)
                }
                actionResult.roadNetworks?.let { networks -> it.commands.syncRoadNetworks(networks) }
                actionResult.cityUpgradeRewardRequests?.let { requests ->
                    requests.forEach { request -> it.commands.addCityUpgradeRequest(request) }
                }
            }
        }.awaitAll()
        actionResult.gameEffects
    }

actual fun chooseCityUpgradeReward(pos: Position, reward: CityUpgradeReward): List<GameEffect> =
    runBlocking {
        val (actionResult, bundle) = invokeOnFirstMod { commands.chooseCityUpgradeReward(pos, reward) }
        val gameDiffs = try {
            withContext(bundle.dispatcher) {
                actionResult.gameEffects.map {
                    bundle.commands.calculateGameDiff(it)
                }
            }
        } catch (e: ZiplineException) {
            if (!e.toString().contains("NotImplementedError")) throw e

            val defaultBundle = serverBundles.last()
            withContext(defaultBundle.dispatcher) {
                actionResult.gameEffects.map {
                    defaultBundle.commands.calculateGameDiff(it)
                }
            }
        }
        serverBundles.filter { it !== bundle }.map {
            async(it.dispatcher) {
                it.commands.removeFirstCityUpgradeRequest()
                gameDiffs.forEach { gameDiff ->
                    it.commands.syncGameDiff(gameDiff)
                }
                actionResult.roadNetworks?.let { networks -> it.commands.syncRoadNetworks(networks) }
                actionResult.cityUpgradeRewardRequests?.let { requests ->
                    requests.forEach { request -> it.commands.addCityUpgradeRequest(request) }
                }
            }
        }.awaitAll()
        actionResult.gameEffects
    }

suspend fun <T> invokeOnFirstMod(callback: ServerBundle.() -> T): Pair<T, ServerBundle> {
    serverBundles.forEach { serverBundle ->
        try {
            return withContext(serverBundle.dispatcher) {
                serverBundle.callback() to serverBundle
            }
        } catch (e: ZiplineException) {
            if (!e.toString().contains("NotImplementedError")) throw e
        }
    }
    error("No mod was able to perform the action.")
}
