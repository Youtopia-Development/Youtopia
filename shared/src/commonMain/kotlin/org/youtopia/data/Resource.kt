package org.youtopia.data

import kotlinx.serialization.Serializable

@Serializable
sealed interface Resource {

    val collectActionName: String?

    @Serializable
    data object Game : Resource {
        override val collectActionName = "Hunting"
    }

    @Serializable
    data object Fruit : Resource {
        override val collectActionName = "Harvest Fruit"
    }

    @Serializable
    data object Crop : Resource {
        override val collectActionName = null
    }

    @Serializable
    data object Metal : Resource {
        override val collectActionName = null
    }

    @Serializable
    data object Fish : Resource {
        override val collectActionName = "Fishing"
    }

    @Serializable
    data object Starfish : Resource {
        override val collectActionName = "Gather Stars"
    }
}
