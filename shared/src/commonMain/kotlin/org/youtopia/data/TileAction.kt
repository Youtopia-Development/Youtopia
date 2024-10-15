package org.youtopia.data

import kotlinx.serialization.Serializable

@Serializable
sealed interface TileAction {

    val availability: TileActionAvailability
    val name: String

    @Serializable
    data class Recruit(
        val unit: Troop,
        override val availability: TileActionAvailability,
        override val name: String = "Recruit",
    ) : TileAction

    @Serializable
    data class Build(
        val building: Building,
        override val availability: TileActionAvailability,
        override val name: String = building.name,
    ) : TileAction

    @Serializable
    data class CollectResource(
        val resource: Resource,
        val tribe: Tribe,
        override val availability: TileActionAvailability,
        override val name: String = "${resource.collectActionName}",
    ) : TileAction

    @Serializable
    data class BuildRoad(
        override val availability: TileActionAvailability,
        override val name: String = "Road",
    ) : TileAction

    @Serializable
    data class ClearForest(
        override val availability: TileActionAvailability,
        override val name: String = "Clear Forest",
    ) : TileAction

    @Serializable
    data class GrowForest(
        override val availability: TileActionAvailability,
        override val name: String = "Grow Forest",
    ) : TileAction

    @Serializable
    data class BurnForest(
        override val availability: TileActionAvailability,
        override val name: String = "Burn Forest",
    ) : TileAction

    @Serializable
    data class DestroyBuilding(
        val building: Building,
        override val availability: TileActionAvailability,
        override val name: String = "Destroy",
    ) : TileAction
}
