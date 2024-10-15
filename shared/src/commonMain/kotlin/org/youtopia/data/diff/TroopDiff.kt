package org.youtopia.data.diff

import kotlinx.serialization.Serializable
import org.youtopia.data.CityId
import org.youtopia.data.Skill

@Serializable
data class TroopDiff(
    val name: String? = null,
    val readyToMove: Boolean? = null,
    val readyToAttack: Boolean? = null,
    val hp: Int? = null,
    val maxHp: Int? = null,
    val attack: Int? = null,
    val defense: Int? = null,
    val range: Int? = null,
    val movement: Int? = null,
    val experience: Int? = null,
    val veteranExperience: Int? = null,
    val isVeteran: Boolean? = null,
    val price: Int? = null,
    val city: CityId? = null,
    val skills: List<Skill>? = null,
)
