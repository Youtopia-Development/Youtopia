package org.youtopia.server.zipline

import org.youtopia.data.CityUpgradeReward
import org.youtopia.data.Game
import org.youtopia.data.GameEffect
import org.youtopia.data.GeneralAction
import org.youtopia.data.Position
import org.youtopia.data.TileAction

expect fun initialiseServerBundles(modsDirectory: String)

expect fun generateGame(size: Pair<Int, Int>): Game

expect fun requestAvailableTileActions(tilePosition: Position): List<TileAction>

expect fun performTileAction(tilePosition: Position, action: TileAction): List<GameEffect>

expect fun performGeneralAction(action: GeneralAction): List<GameEffect>

expect fun chooseCityUpgradeReward(pos: Position, reward: CityUpgradeReward): List<GameEffect>
