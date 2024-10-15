package org.youtopia.data

import kotlinx.serialization.Serializable
import org.youtopia.data.diff.TroopDiff

@Serializable
data class Troop(
    val name: String,
    val readyToMove: Boolean,
    val readyToAttack: Boolean,
    val hp: Int,
    val maxHp: Int,
    val attack: Int,
    val defense: Int,
    val range: Int,
    val movement: Int,
    val experience: Int,
    val veteranExperience: Int?,
    val isVeteran: Boolean,
    val price: Int,
    val city: CityId,
    val skills: List<Skill>,
) {
    fun update(diff: TroopDiff): Troop = Troop(
        name = diff.name ?: name,
        readyToMove = diff.readyToMove ?: readyToMove,
        readyToAttack = diff.readyToAttack ?: readyToAttack,
        hp = diff.hp ?: hp,
        maxHp = diff.maxHp ?: maxHp,
        attack = diff.attack ?: attack,
        defense = diff.defense ?: defense,
        range = diff.range ?: range,
        movement = diff.movement ?: movement,
        experience = diff.experience ?: experience,
        veteranExperience = diff.veteranExperience ?: veteranExperience,
        isVeteran = diff.isVeteran ?: isVeteran,
        price = diff.price ?: price,
        city = diff.city ?: city,
        skills = diff.skills ?: skills,
    )
}
