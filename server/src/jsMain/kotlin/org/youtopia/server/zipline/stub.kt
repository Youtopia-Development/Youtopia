package org.youtopia.server.zipline

import org.youtopia.data.CityUpgradeReward
import org.youtopia.data.Game
import org.youtopia.data.GameEffect
import org.youtopia.data.GeneralAction
import org.youtopia.data.Position
import org.youtopia.data.TileAction

actual fun initialiseServerBundles(modsDirectory: String): Unit = throw NotImplementedError()

actual fun generateGame(size: Pair<Int, Int>): Game = throw NotImplementedError()

actual fun requestAvailableTileActions(tilePosition: Position): List<TileAction> = throw NotImplementedError()

actual fun performTileAction(tilePosition: Position, action: TileAction): List<GameEffect> = throw NotImplementedError()

actual fun performGeneralAction(action: GeneralAction): List<GameEffect> = throw NotImplementedError()

actual fun chooseCityUpgradeReward(pos: Position, reward: CityUpgradeReward): List<GameEffect> = throw NotImplementedError()
