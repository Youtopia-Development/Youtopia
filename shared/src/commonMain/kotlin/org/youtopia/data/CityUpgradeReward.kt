package org.youtopia.data

import kotlinx.serialization.Serializable

@Serializable
enum class CityUpgradeReward(val printedName: String) {
    WORKSHOP("Workshop"),
    EXPLORER("Explorer"),
    CITY_WALL("City Wall"),
    RESOURCES("Resources"),
    POPULATION_GROWTH("Population Growth"),
    BORDER_GROWTH("Border Growth"),
    PARK("Park"),
    SUPER_UNIT("Super Unit"),
}
