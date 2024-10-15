package org.youtopia.data

import kotlinx.serialization.Serializable

@Serializable
data class CityUpgradeRewardRequest(
    val pos: Position,
    val rewards: List<CityUpgradeReward>,
)

fun upgradeRewardsForLevel(level: Int) = when(level) {
    2 -> listOf(CityUpgradeReward.WORKSHOP, CityUpgradeReward.EXPLORER)
    3 -> listOf(CityUpgradeReward.CITY_WALL, CityUpgradeReward.RESOURCES)
    4 -> listOf(CityUpgradeReward.POPULATION_GROWTH, CityUpgradeReward.BORDER_GROWTH)
    else -> listOf(CityUpgradeReward.PARK, CityUpgradeReward.SUPER_UNIT)
}
