package org.youtopia.data

import kotlinx.serialization.Serializable

@Serializable
sealed interface GameEffect {

    @Serializable
    data class AddPopulation(
        val from: Int,
        val to: Int,
    ) : GameEffect

    @Serializable
    data class RemovePopulation(
        val from: Int,
    ) : GameEffect

    @Serializable
    data class UpgradeCity(
        val cell: Position,
        val city: Building.City,
        val targetPopulation: Int,
        val score: Int,
        val upgradeRewards: List<CityUpgradeReward>,
    ) : GameEffect

    @Serializable
    data class Build(
        val cell: Position,
        val building: Building,
    ) : GameEffect

    @Serializable
    data class BuildRoad(
        val cell: Position,
    ) : GameEffect

    @Serializable
    data class DestroyBuilding(
        val cell: Position,
        val underlyingResource: Resource?,
        val underlyingResourceTribe: Tribe?,
    ) : GameEffect

    @Serializable
    data class UpgradeBuilding(
        val cell: Position,
        val building: Building,
    ) : GameEffect

    @Serializable
    data class DowngradeBuilding(
        val cell: Position,
        val building: Building,
    ) : GameEffect

    @Serializable
    data class UpdateStars(
        val delta: Int,
    ) : GameEffect

    @Serializable
    data class SwapTerrain(
        val cell: Position,
        val newTerrain: Terrain,
        val tribe: Tribe,
    ) : GameEffect

    @Serializable
    data class RemoveResource(
        val pos: Position,
    ) : GameEffect

    @Serializable
    data class AddResource(
        val pos: Position,
        val resource: Resource,
        val tribe: Tribe,
    ) : GameEffect

    @Serializable
    data class ChartWaterRoutes(
        val routes: Set<List<Position>>,
    ) : GameEffect

    @Serializable
    data class EraseWaterRoutes(
        val routes: Set<List<Position>>,
    ) : GameEffect

    @Serializable
    data class AddWorkshop(
        val pos: Position,
        val city: Building.City,
    ) : GameEffect

    @Serializable
    data class AddPark(
        val pos: Position,
        val city: Building.City,
    ) : GameEffect

    @Serializable
    data class AddWall(
        val pos: Position,
        val city: Building.City,
    ) : GameEffect

    @Serializable
    data class ConnectToCapital(
        val pos: Position,
    ) : GameEffect

    @Serializable
    data class DisconnectFromCapital(
        val pos: Position,
    ) : GameEffect

    @Serializable
    data class UpdateTerritory(
        val city: Position,
        val territory: Set<Position>,
    ) : GameEffect

    @Serializable
    data class UpdateIncome(
        val delta: Int,
    ) : GameEffect

    @Serializable
    data class DecrementTempleCounter(
        val pos: Position,
    ) : GameEffect

    @Serializable
    data object PassTurn : GameEffect
}
