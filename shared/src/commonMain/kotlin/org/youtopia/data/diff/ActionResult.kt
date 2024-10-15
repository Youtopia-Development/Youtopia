package org.youtopia.data.diff

import kotlinx.serialization.Serializable
import org.youtopia.data.CityUpgradeRewardRequest
import org.youtopia.data.GameEffect
import org.youtopia.data.RoadNetwork

@Serializable
data class ActionResult(
    val gameEffects: List<GameEffect>,
    val roadNetworks: MutableSet<RoadNetwork>? = null,
    val cityUpgradeRewardRequests: List<CityUpgradeRewardRequest>? = null,
)
