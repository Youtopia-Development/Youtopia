package org.youtopia.server.gameeffect

import org.youtopia.server.api.Server
import org.youtopia.data.GameEffect
import org.youtopia.data.diff.GameDiff

fun applyGameEffect(action: GameEffect): GameDiff {
    val game = Server.game
    return when (action) {
        is GameEffect.AddPopulation -> addPopulation(action, game)
        is GameEffect.UpgradeCity -> upgradeCity(action, game)
        is GameEffect.AddResource -> addResource(action)
        is GameEffect.Build -> build(action, game)
        is GameEffect.BuildRoad -> buildRoad(action)
        is GameEffect.DestroyBuilding -> destroyBuilding(action, game)
        is GameEffect.DowngradeBuilding -> downgradeBuilding(action)
        is GameEffect.RemovePopulation -> removePopulation(action, game)
        is GameEffect.RemoveResource -> removeResource(action)
        is GameEffect.SwapTerrain -> swapTerrain(action)
        is GameEffect.UpdateStars -> updateStars(action, game)
        is GameEffect.UpgradeBuilding -> upgradeBuilding(action)
        is GameEffect.PassTurn -> passTurn(game)
        is GameEffect.ChartWaterRoutes -> chartWaterRoutes(action)
        is GameEffect.EraseWaterRoutes -> eraseWaterRoutes(action)
        is GameEffect.AddWorkshop -> addWorkshop(action, game)
        is GameEffect.AddPark -> addPark(action, game)
        is GameEffect.AddWall -> addWall(action, game)
        is GameEffect.ConnectToCapital -> connectToCapital(action, game)
        is GameEffect.DisconnectFromCapital -> disconnectFromCapital(action, game)
        is GameEffect.UpdateTerritory -> updateTerritory(action, game)
        is GameEffect.UpdateIncome -> updateIncome(action, game)
        is GameEffect.DecrementTempleCounter -> decrementTempleCounter(action, game)
    }
}
