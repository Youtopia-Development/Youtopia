package org.youtopia.data.diff

import kotlinx.serialization.Serializable
import org.youtopia.data.Building
import org.youtopia.data.CityId
import org.youtopia.data.PlayerId
import org.youtopia.data.Resource
import org.youtopia.data.Terrain
import org.youtopia.data.Tribe

@Serializable
data class TileDiff(
    val terrain: Terrain? = null,
    val tribe: Tribe? = null,
    val resource: Resource? = null,
    val waterRoute: Boolean? = null,
    val road: Boolean? = null,
    val building: Building? = null,
    val owner: PlayerId? = null,
    val city: CityId? = null,
    val removeResource: Boolean = false,
    val removeBuilding: Boolean = false,
    val removeOwner: Boolean = false,
    val removeCity: Boolean = false,
)
