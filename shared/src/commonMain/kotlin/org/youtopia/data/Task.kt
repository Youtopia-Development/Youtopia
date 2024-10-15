package org.youtopia.data

import kotlinx.serialization.Serializable

// FIXME: this approach breaks zipline in the weirdest way
//@Serializable
//open class Task(
//    val requirement: Int,
//    val monument: MonumentType,
//) {
//    @Serializable object Pacifist : Task(5, MonumentType.AltarOfPeace)
//    @Serializable object Wealth : Task(100, MonumentType.EmperorsTomb)
//    @Serializable object Explorer : Task(4, MonumentType.EyeOfGod)
//    @Serializable object Killer : Task(10, MonumentType.GateOfPower)
//    @Serializable object Trade : Task(5, MonumentType.GrandBazaar)
//    @Serializable object Metropolis : Task(5, MonumentType.ParkOfFortune)
//    @Serializable object Genius : Task(25, MonumentType.TowerOfWisdom)
//}
@Serializable
enum class Task {
    PACIFIST,
    WEALTH,
    EXPLORER,
    KILLER,
    TRADE,
    METROPOLIS,
    GENIUS,
}
