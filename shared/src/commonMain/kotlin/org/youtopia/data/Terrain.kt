package org.youtopia.data

import kotlinx.serialization.Serializable

@Serializable
enum class Terrain {
    FIELD,
    FOREST,
    MOUNTAIN,
    WATER,
    OCEAN,
}
