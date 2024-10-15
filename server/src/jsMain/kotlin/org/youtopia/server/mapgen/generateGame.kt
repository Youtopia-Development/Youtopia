package org.youtopia.server.mapgen

import org.youtopia.data.Building
import org.youtopia.data.City
import org.youtopia.data.Direction
import org.youtopia.data.Game
import org.youtopia.data.Player
import org.youtopia.data.PlayerId
import org.youtopia.data.Position
import org.youtopia.data.Resource
import org.youtopia.data.RoadNetwork
import org.youtopia.data.Skill
import org.youtopia.data.Terrain
import org.youtopia.data.Tile
import org.youtopia.data.Tribe
import org.youtopia.data.Troop
import org.youtopia.server.utils.SimplexNoise.noise
import org.youtopia.server.utils.modifyAt
import org.youtopia.server.utils.neighbours
import org.youtopia.server.utils.randomWeighted
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

const val LAND_FACTOR = 0.1
const val NOISE_SCALE = 20.0

@Suppress("LongMethod", "CyclomaticComplexMethod", "NestedBlockDepth", "ComplexCondition") // TODO: shorten this :/
fun generateGameImpl(size: Pair<Int, Int>): Pair<Game, Set<RoadNetwork>> {
    val (width, height) = size
    val length = width * height

    val heightMap = (0..<length).map {
        noise(it % width / NOISE_SCALE, it / width / NOISE_SCALE) +
            noise(it % width / NOISE_SCALE * 2.1, it / width / NOISE_SCALE * 2.1) / 4 * 3 +
            noise(it % width / NOISE_SCALE * 4.3, it / width / NOISE_SCALE * 4.3) / 16 * 9 +
            noise(it % width / NOISE_SCALE * 8.7, it / width / NOISE_SCALE * 8.7) / 64 * 27
    }

    val biomeCount = sqrt(length.toDouble()).roundToInt()
    val biomeOrigins = (0..<length).shuffled().subList(0, biomeCount)
    val biomes = biomeOrigins.map {
        30 * (1 - it / width / (height - 1.0)) + Random.nextInt(-5, 6)
    }.map { temperature ->
        val biomeTempDistances = Biome.entries.map { biome ->
            abs(1 / (biome.temperature - temperature) / (biome.temperature - temperature) / (biome.temperature - temperature))
        }
        val biomeTempDistancesSize = biomeTempDistances.sum()
        var rand = Random.nextDouble(biomeTempDistancesSize)
        var i = 0
        while (rand > biomeTempDistances[i]) {
            rand -= biomeTempDistances[i]
            i++
        }
        Biome.entries[i]
    }

    val mutableBiomeMap: MutableList<Biome?> = MutableList(length) { null }
    for (i in 0..<biomeCount) {
        mutableBiomeMap[biomeOrigins[i]] = biomes[i]
    }
    val doneCells = biomeOrigins.toMutableSet()
    val activeCells = biomeOrigins.map { mutableListOf(it) }

    while (doneCells.size != length) {
        for (i in 0..<biomeCount) {
            if (activeCells[i].isEmpty()) continue

            val randCell = activeCells[i].randomWeighted()
            val neighbours = neighbours(randCell, size)
            val validNeighbours = neighbours.filter { it !in doneCells && heightMap[it] >= -LAND_FACTOR }.ifEmpty {
                neighbours.filter { it !in doneCells }
            }
            if (validNeighbours.isNotEmpty()) {
                val randomNeighbour = validNeighbours.randomWeighted()
                mutableBiomeMap[randomNeighbour] = biomes[i]
                activeCells[i].add(randomNeighbour)
                doneCells.add(randomNeighbour)
            } else {
                activeCells[i].remove(randCell)
            }
        }
    }
    val biomeMap = mutableBiomeMap.filterNotNull()
    if (biomeMap.size != mutableBiomeMap.size) error("Some tiles do not have specified biomes!")

    var vacantTilesCount = 0
    val vacantTiles = mutableSetOf<Int>()
    val populationMap = heightMap.mapIndexed { i, terrainHeight ->
        if (i % width == 0 || i % width == width - 1 || i / width == 0 || i / width == height - 1)
            PopulationState.BORDER
        else if (terrainHeight >= -LAND_FACTOR) {
            vacantTilesCount++
            vacantTiles.add(i)
            PopulationState.VACANT
        }
        else PopulationState.WATER
    }.toMutableList()

    val villages = mutableListOf<Position>()
    while (vacantTilesCount > 0) {
        val randomVacantTile = vacantTiles.random()
        populationMap[randomVacantTile] = PopulationState.TAKEN
        villages.add(randomVacantTile)

        vacantTiles.remove(randomVacantTile)
        vacantTilesCount--
        neighbours(randomVacantTile, size).forEach {
            if (populationMap[it] == PopulationState.VACANT) {
                vacantTiles.remove(it)
                vacantTilesCount--
            }
            populationMap[it] = PopulationState.INNER
        }
        circle(randomVacantTile, 2, size).forEach {
            if (populationMap[it] != PopulationState.INNER) {
                if (populationMap[it] == PopulationState.VACANT) {
                    vacantTiles.remove(it)
                    vacantTilesCount--
                }
                populationMap[it] = PopulationState.OUTER
            }
        }
    }

    val terrainMap = heightMap.mapIndexed { i, terrainHeight ->
        val biome = biomeMap[i]
        if (populationMap[i] == PopulationState.TAKEN) {
            Terrain.FIELD
        }
        else if (terrainHeight >= -LAND_FACTOR)
            listOf(Terrain.FIELD, Terrain.FOREST, Terrain.MOUNTAIN)
                .randomWeighted(biome.fieldModifier, biome.forestModifier, biome.mountainModifier)
        else if ((i % width != 0 && heightMap[i - 1] >= -LAND_FACTOR) ||
            (i % width != width - 1 && heightMap[i + 1] >= -LAND_FACTOR) ||
            (i / width != 0 && heightMap[i - width] >= -LAND_FACTOR) ||
            (i / width != height - 1 && heightMap[i + width] >= -LAND_FACTOR)
        )
            Terrain.WATER
        else
            Terrain.OCEAN
    }

    val ruinMap = populationMap.mapIndexed { index, state ->
        when (state) {
            PopulationState.WATER,
            PopulationState.BORDER,
            PopulationState.OUTER,
            -> if (terrainMap[index] == Terrain.WATER) PopulationState.TAKEN else PopulationState.VACANT

            PopulationState.VACANT,
            PopulationState.INNER,
            PopulationState.TAKEN,
            -> PopulationState.TAKEN
        }
    }.toMutableList()
    val ruinVacantTiles = ruinMap.mapIndexedNotNull { index, state -> index.takeIf { state == PopulationState.VACANT } }.toMutableSet()
    val ruinTiles = mutableSetOf<Position>()
    repeat(length / 40) {
        val ruinTile = ruinVacantTiles.random()
        ruinTiles.add(ruinTile)
        neighbours(ruinTile, size).forEach { ruinVacantTiles -= it }
    }

    val resourceMap = terrainMap.mapIndexed { i, terrain ->
        val biome = biomeMap[i]
        val populationState = populationMap[i]
        val populationModifier = when (populationState) {
            PopulationState.INNER -> 1f
            PopulationState.OUTER -> 1 / 3f
            else -> 0f
        }
        if (populationModifier > 0) {
            when (terrain) {
                Terrain.FIELD -> {
                    val fruit = Random.nextFloat() < biome.fruitModifier * populationModifier
                    val crop = Random.nextFloat() < biome.cropModifier * populationModifier
                    if (fruit && crop) if (Random.nextFloat() < 0.5f) Resource.Fruit else Resource.Crop
                    else if (fruit) Resource.Fruit
                    else if (crop) Resource.Crop
                    else null
                }
                Terrain.FOREST ->
                    if (Random.nextFloat() < biome.gameModifier * populationModifier) Resource.Game else null
                Terrain.MOUNTAIN ->
                    if (Random.nextFloat() < biome.metalModifier * populationModifier) Resource.Metal else null
                Terrain.WATER, Terrain.OCEAN ->
                    if (Random.nextFloat() < biome.fishModifier * populationModifier) Resource.Fish else null
            }
        } else null
    }

    val capitalTile = villages.random()
    val cityMap = populationMap.mapIndexed { i, populationState ->
        if (populationState == PopulationState.TAKEN) Building.City(
            name = generateCityName(),
            tribe = Tribe.IMPERIUS,
            isCapital = i == capitalTile,
        ) else null
    }
    val cityCells = cityMap.mapIndexedNotNull { index, city -> index.takeIf { city != null } }

    var cityCount = 0

    var game = Game(
        tiles = List(length) {
            if (it in setOf(0, width - 1, length - width, length - 1)) {
                Tile(
                    terrain = Terrain.FIELD,
                    tribe = biomeMap[it].tribe,
                    building = Building.Lighthouse(
                        direction = when (it) {
                            0 -> Direction.WEST
                            width - 1 -> Direction.SOUTH
                            length - width -> Direction.NORTH
                            length - 1 -> Direction.EAST
                            else -> error("Invalid lighthouse position $it")
                        },
                        seenBy = listOf(0),
                    ),
                    owner = null,
                )
            } else {
                val isCity = cityMap[it] != null
                Tile(
                    terrain = terrainMap[it],
                    tribe = biomeMap[it].tribe,
                    resource = if (it in ruinTiles) null else resourceMap[it],
                    building = cityMap[it] ?: if (it in ruinTiles) Building.Ruin else null,
                    owner = if (isCity) 1 else null,
                    road = isCity,
                ).also {
                    if (isCity) cityCount++
                }
            }
        },
        size = size,
        players = listOf(
            Player(
                id = 0,
                name = "Player1",
                tribe = Tribe.IMPERIUS,
                color = 0x0000FF,
                score = cityCount * (50 + 20 * 9) + length * 5 + 100 * 5 + 200 * 10 + 300 * 10,
                stars = 5,
                income = cityCount,
                fog = emptySet(),
                cities = cityCells.associateWith { cell ->
                    City(
                        name = cityMap[cell]?.name ?: "",
                        territory = emptySet(),
                        isCapital = cell == capitalTile,
                    )
                },
                troops = mapOf(
                    capitalTile to Troop(
                        name = "Warrior",
                        readyToMove = true,
                        readyToAttack = true,
                        hp = 10,
                        maxHp = 10,
                        attack = 2,
                        defense = 2,
                        range = 1,
                        movement = 1,
                        experience = 0,
                        veteranExperience = 3,
                        isVeteran = false,
                        price = 2,
                        city = 0,
                        skills = listOf(Skill.Dash, Skill.Fortify),
                    )
                ),
            ),
        ),
        turn = 0,
        currentPlayerIndex = 0,
    )

    cityCells.forEach { cell ->
        game = game.capture3x3Area(playerId = 0, center = cell, cityPos = cell)
    }
    return game to cityCells.map { RoadNetwork(
        roads = setOf(it),
        capital = if (capitalTile == it) it else null,
        cities = setOf(it),
    ) }.toSet()
}


// TODO: this is slow for no(?) reason
private fun Game.capture3x3Area(playerId: PlayerId, center: Position, cityPos: Position): Game {
    val player = players[playerId]
    val city = player.cities[center] ?: error("City not found at position $center.")
    return copy(
        tiles = tiles.modifyAt(
            neighbours(center, size) + center
        ) {
            copy(owner = playerId, city = cityPos)
        },
        players = players.filterNot { it.id == playerId } + player.copy(
            cities = player.cities.filterNot { it.key == cityPos } + (center to city.copy(
                territory = city.territory + neighbours(center, size) + center
            ))
        )
    )
}

private fun generateCityName(): String {
    val syllables = listOf("mo", "nu", "ma", "lus", "te", "ro", "ca", "mus", "ica", "lo", "re", "pi", "ip", "sum", "do", "res")
    val length = 3 + 3 * Random.nextFloat()
    val name = StringBuilder()
    while(name.length < length) {
        name.append(syllables.random())
    }
    return name.toString().replaceFirstChar { it.uppercaseChar() }
}

enum class Biome(
    val tribe: Tribe,
    val temperature: Int,
    val fieldModifier: Float = 0.5f,
    val forestModifier: Float = 0.4f,
    val mountainModifier: Float = 0.15f,
    val cropModifier: Float = 0.5f,
    val fishModifier: Float = 0.5f,
    val fruitModifier: Float = 0.5f,
    val gameModifier: Float = 0.5f,
    val metalModifier: Float = 0.8f,
) {
    XIN_XI_BIOME(
        tribe = Tribe.XIN_XI,
        temperature = 15,
        mountainModifier = 0.225f,
        metalModifier = 1.2f,
    ),
    IMPERIUS_BIOME(
        tribe = Tribe.IMPERIUS,
        temperature = 15,
        fruitModifier = 1f,
        gameModifier = 0.25f,
    ),
    BARDUR_BIOME(
        tribe = Tribe.BARDUR,
        temperature = 0,
        forestModifier = 0.32f,
        cropModifier = 0f,
    ),
    OUMAJI_BIOME(
        tribe = Tribe.OUMAJI,
        temperature = 30,
        forestModifier = 0.08f,
        mountainModifier = 0.075f,
        gameModifier = 0.1f,
    ),
    KICKOO_BIOME(
        tribe = Tribe.KICKOO,
        temperature = 25,
        mountainModifier = 0.075f,
        fishModifier = 0.75f,
    ),
    HOODRICK_BIOME(
        tribe = Tribe.HOODRICK,
        temperature = 5,
        forestModifier = 0.6f,
        mountainModifier = 0.75f,
    ),
    LUXIDOOR_BIOME(
        tribe = Tribe.LUXIDOOR,
        temperature = 20,
    ),
    VENGIR_BIOME(
        tribe = Tribe.VENGIR,
        temperature = 5,
        fishModifier = 0.05f,
        fruitModifier = 0.05f,
        gameModifier = 0.05f,
        metalModifier = 1.6f,
    ),
    ZEBASI_BIOME(
        tribe = Tribe.ZEBASI,
        temperature = 25,
        forestModifier = 0.2f,
        mountainModifier = 0.075f,
        fruitModifier = 0.25f,
    ),
    AI_MO_BIOME(
        tribe = Tribe.AI_MO,
        temperature = 10,
        mountainModifier = 0.225f,
        cropModifier = 0.05f,
    ),
    QUETZALI_BIOME(
        tribe = Tribe.QUETZALI,
        temperature = 10,
        cropModifier = 0.05f,
        fruitModifier = 1f,
    ),
    YADAKK_BIOME(
        tribe = Tribe.YADAKK,
        temperature = 20,
        forestModifier = 0.25f,
        mountainModifier = 0.075f,
        fruitModifier = 0.75f,
    ),
}

enum class PopulationState {
    WATER,
    BORDER,
    VACANT,
    OUTER,
    INNER,
    TAKEN,
}

private fun circle(center: Int, radius: Int, mapSize: Pair<Int, Int>): Set<Int> = buildSet {
    val (width, height) = mapSize
    val row = center / width
    val column = center % width
    val bottomY = row - radius
    if (bottomY in 0..<height) {
        for (x in column - radius..<column + radius) {
            if (x in 0..<width) add(bottomY * width + x)
        }
    }
    val topY = row + radius
    if (topY in 0..<height) {
        for (x in column + radius downTo column - radius + 1) {
            if (x in 0..<width) add(topY * width + x)
        }
    }
    val leftX = column - radius
    if (leftX in 0..<width) {
        for (y in row + radius downTo row - radius + 1) {
            if (y in 0..<height) add(y * width + leftX)
        }
    }
    val rightX = column + radius
    if (rightX in 0..<width) {
        for (y in row - radius..<row + radius) {
            if (y in 0..<height) add(y * width + rightX)
        }
    }
}

fun round(center: Int, radius: Int, mapSize: Pair<Int, Int>): List<Int> = (1..radius).flatMap {
    circle(center, it, mapSize)
} + center
