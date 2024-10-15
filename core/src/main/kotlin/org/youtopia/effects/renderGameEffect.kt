package org.youtopia.effects

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.youtopia.map.SelectableTiledMap
import org.youtopia.data.GameEffect
import org.youtopia.ui.CityLabel
import org.youtopia.ui.UiStage

suspend fun renderGameEffect(
    action: GameEffect,
    gameSize: Pair<Int, Int>,
    gameEffectQueue: MutableSharedFlow<GameEffect>,
    tileMap: SelectableTiledMap,
    uiStage: UiStage,
    score: MutableStateFlow<Int>,
    stars: MutableStateFlow<Int>,
    income: MutableStateFlow<Int>,
    turn: MutableStateFlow<Int>,
    cityLabels: MutableMap<Int, CityLabel>,
) {
    when (action) {
        is GameEffect.AddPark -> addPark(action, tileMap, cityLabels, uiStage, score, stars, income)
        is GameEffect.AddPopulation -> addPopulation(action, cityLabels, uiStage, score, stars, income)
        is GameEffect.AddResource -> addResource(action, tileMap)
        is GameEffect.AddWall -> addWall(action, tileMap)
        is GameEffect.AddWorkshop -> addWorkshop(action, tileMap, cityLabels, uiStage, stars, income)
        is GameEffect.Build -> build(action, gameSize, tileMap)
        is GameEffect.BuildRoad -> buildRoad(action, gameSize, tileMap)
        is GameEffect.ChartWaterRoutes -> chartWaterRoutes(action, gameSize, tileMap)
        is GameEffect.ConnectToCapital -> connectToCapital(action, cityLabels)
        is GameEffect.DestroyBuilding -> destroyBuilding(action, gameSize, tileMap)
        is GameEffect.DisconnectFromCapital -> disconnectFromCapital(action, cityLabels)
        is GameEffect.DowngradeBuilding -> downgradeBuilding(action, tileMap)
        is GameEffect.EraseWaterRoutes -> eraseWaterRoutes(action, gameSize, tileMap)
        is GameEffect.PassTurn -> passTurn(uiStage, turn)
        is GameEffect.RemovePopulation -> removePopulation(action, cityLabels, uiStage, score, stars, income)
        is GameEffect.RemoveResource -> removeResource(action, tileMap)
        is GameEffect.SwapTerrain -> swapTerrain(action, tileMap)
        is GameEffect.UpdateStars -> updateStars(action, uiStage, stars, income)
        is GameEffect.UpdateTerritory -> updateTerritory(action, gameSize, tileMap, uiStage, score)
        is GameEffect.UpgradeBuilding -> upgradeBuilding(action, tileMap)
        is GameEffect.UpgradeCity -> upgradeCity(action, cityLabels, gameEffectQueue, tileMap, uiStage, score, stars, income)
        is GameEffect.UpdateIncome -> updateIncome(action, uiStage, stars, income)
        is GameEffect.DecrementTempleCounter -> Unit
    }
    delay(100) // TODO: replace with animations
}
