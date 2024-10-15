package org.youtopia.data

import kotlinx.serialization.Serializable
import org.youtopia.data.diff.TileDiff

@Serializable
data class Tile(
    val terrain: Terrain,
    val tribe: Tribe,
    val resource: Resource? = null,
    val road: Boolean = false,
    val waterRoute: Boolean = false,
    val building: Building? = null,
    val owner: PlayerId? = null,
    val city: Position? = null,
) {
    fun update(diff: TileDiff): Tile = Tile(
        terrain = diff.terrain ?: terrain,
        tribe = diff.tribe ?: tribe,
        resource = if (diff.removeResource) null else diff.resource ?: resource,
        road = diff.road ?: road,
        waterRoute = diff.waterRoute ?: waterRoute,
        building = if (diff.removeBuilding) null else diff.building ?: building,
        owner = if (diff.removeOwner) null else diff.owner ?: owner,
        city = if (diff.removeCity) null else diff.city ?: city,
    )
}
