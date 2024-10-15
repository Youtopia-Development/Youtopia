package org.youtopia.map

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import org.youtopia.data.Building
import org.youtopia.data.PlayerId
import org.youtopia.data.Position
import org.youtopia.utils.getBuildingAtlasName
import org.youtopia.utils.tilesAtlas
import kotlin.random.Random
import com.badlogic.gdx.utils.Array as GdxArray

class GameCell(
    tile: TiledMapTile,
    val x: Int,
    val y: Int,
    private val mapWidth: Int,
) : TiledMapTileLayer.Cell() {
    init {
        setTile(tile)
    }

    var topLeftBorderObject: TiledMapTileMapObject? = null
    var topRightBorderObject: TiledMapTileMapObject? = null
    var terrainObject: TiledMapTileMapObject? = null
    val roadObjects: MutableList<TiledMapTileMapObject?> = MutableList(9) { null }
    var resourceObject: TiledMapTileMapObject? = null
    var buildingObject: TiledMapTileMapObject? = null
    val buildingPartObjects: MutableList<TiledMapTileMapObject> = mutableListOf()
    var bottomLeftBorderObject: TiledMapTileMapObject? = null
    var bottomRightBorderObject: TiledMapTileMapObject? = null

    // These attributes are used to correctly instantiate graphics for roads and water routes
    var roadConnected: Boolean = false
    var isRoad: Boolean = false
    var isPort: Boolean = false
    var isBridge: Boolean = false

    var isCity: Boolean = false
    var city: Position? = null
    var owner: PlayerId? = null

    fun addTerrain(
        pos: Position,
        atlasName: String,
    ) {
        terrainObject = createMapObject(pos, atlasName)
    }

    fun addResource(
        pos: Position,
        atlasName: String,
    ) {
        resourceObject = createMapObject(pos, atlasName)
    }

    fun addBuilding(
        pos: Position,
        building: Building,
    ) {
        buildingObject = createMapObject(
            pos,
            getBuildingAtlasName(building),
            atlasIndex = if (building is Building.UpgradeableBuilding<*>) building.level else null,
        )
    }

    fun addPath(
        pos: Position,
        direction: Int,
        pathType: Path,
    ) {
        roadObjects[direction] = createMapObject(pos, pathType.name.lowercase(), direction)
    }

    fun addBorderMapObject(
        i: Int,
        direction: BorderDirection,
    ) {
        val animation = GdxArray((0..33).map {
            StaticTiledMapTile(tilesAtlas.findRegion("border", it))
        }.toTypedArray())
        val obj = TiledMapTileMapObject(
            InterruptableAnimatedTiledMapTile(1 / 20f, animation),
            direction == BorderDirection.BOTTOM_RIGHT || direction == BorderDirection.BOTTOM_LEFT,
            direction == BorderDirection.TOP_RIGHT || direction == BorderDirection.BOTTOM_RIGHT,
        )

        val xOffset = when (direction) {
            BorderDirection.TOP_LEFT -> 120
            BorderDirection.TOP_RIGHT -> 375
            BorderDirection.BOTTOM_LEFT -> 120
            BorderDirection.BOTTOM_RIGHT -> 375
        }

        val yOffset = when (direction) {
            BorderDirection.TOP_LEFT -> 333
            BorderDirection.TOP_RIGHT -> 333
            BorderDirection.BOTTOM_LEFT -> 178
            BorderDirection.BOTTOM_RIGHT -> 178
        }

        obj.x = (i % mapWidth * HALF_TILE_WIDTH) + (i / mapWidth * HALF_TILE_WIDTH) + xOffset
        obj.y = (i / mapWidth * HALF_TILE_HEIGHT) - (i % mapWidth * HALF_TILE_HEIGHT) + yOffset

        obj.properties.put("color", Color.BLUE)

        when (direction) {
            BorderDirection.TOP_LEFT -> topLeftBorderObject = obj
            BorderDirection.TOP_RIGHT -> topRightBorderObject = obj
            BorderDirection.BOTTOM_LEFT -> bottomLeftBorderObject = obj
            BorderDirection.BOTTOM_RIGHT -> bottomRightBorderObject = obj
        }
    }

    @Suppress("LongMethod")
    fun addCity(
        pos: Position,
        city: Building.City,
    ) {
        isCity = true

        buildingPartObjects.clear()

        val housingLength = when (city.level) {
            1 -> 2
            2, 3, 4 -> 3
            else -> 4
        }
        val verticalShift = when (city.level) {
            1 -> 0
            2, 3, 4 -> 35
            else -> 80
        }
        val availableHouses = when (city.level) {
            1 -> listOf(0)
            2 -> listOf(0, 1)
            3 -> listOf(0, 1, 2)
            4 -> listOf(0, 1, 2, 3)
            else -> listOf(0, 1, 2, 3, 4)
        }

        val houseCount = city.level * city.level * 8 / 5 + city.level * 4 - 1
        val districtsCount = housingLength * housingLength

        val districts: List<MutableList<House>> = List(districtsCount) { mutableListOf() }
        var districtIndex = 0
        repeat(houseCount) {
            districts[districtIndex].add(House.REGULAR)
            districtIndex++
            if (Random.nextFloat() < 1 / 3f) districtIndex++
            districtIndex %= districtsCount
        }

        repeat(city.parksCount) {
            val districtNumber = Random.nextInt(districtsCount)
            districts[districtNumber].add(House.PARK)
        }

        if (city.hasWorkshop) {
            districts.random().add(House.WORKSHOP)
        }

        if (city.isCapital) {
            districts.last().add(House.CAPITAL)
        }

        for (districtNumber in 0..<districtsCount) {
            val x = districtNumber % housingLength
            val y = districtNumber / housingLength
            districts[districtNumber].forEachIndexed { floor, house ->
                val atlasName = when (house) {
                    House.REGULAR -> "house_${city.tribe.name.lowercase()}"
                    House.WORKSHOP -> "workshop"
                    House.PARK -> "park"
                    House.CAPITAL -> "capital_${city.tribe.name.lowercase()}"
                    House.EMBASSY -> TODO()
                }
                val atlasIndex = if (house == House.REGULAR) availableHouses.random() else null
                buildingPartObjects.add(createMapObject(
                    pos,
                    atlasName,
                    atlasIndex,
                    xOffset = 275 + x * 65 - y * 65,
                    yOffset = 340 - x * 39 - y * 39 + floor * 50 + verticalShift,
                ))
            }
        }

        if (city.hasWalls) {
            buildingPartObjects.add(createMapObject(pos, "walls", xOffset = 120, yOffset = 180))
        }
    }

    fun addMarket(
        pos: Position,
        market: Building.Market,
    ) {
        buildingPartObjects.clear()

        buildingPartObjects.add(createMapObject(pos, "market_base", xOffset = 310, yOffset = 300))

        if (market.nearForge) {
            buildingPartObjects.add(createMapObject(pos, "market_forge", xOffset = 375, yOffset = 300))
        }

        if (market.nearWindmill) {
            buildingPartObjects.add(createMapObject(pos, "market_windmill", xOffset = 297, yOffset = 277))
        }

        if (market.nearSawmill) {
            buildingPartObjects.add(createMapObject(pos, "market_sawmill", xOffset = 283, yOffset = 282))
        }

        repeat(market.level - 1) {
            buildingPartObjects.add(createMapObject(pos, "market_floor", xOffset = 282, yOffset = 336 + it * 42))
        }

        buildingPartObjects.add(createMapObject(pos, "market_roof", xOffset = 282, yOffset = 267 + market.level * 42))
    }

    private enum class House {
        REGULAR,
        WORKSHOP,
        PARK,
        CAPITAL,
        EMBASSY,
    }

    fun addLighthouse(
        pos: Position,
        lighthouse: Building.Lighthouse,
    ) {
        for (floor in 0..<lighthouse.seenBy.size) {
            buildingPartObjects.add(createMapObject(
                pos,
                "lighthouse_section",
                xOffset = 323,
                yOffset = 305 + floor * 58,
            ))
        }

        buildingPartObjects.add(createMapObject(
            pos,
            "lighthouse_roof",
            xOffset = 323,
            yOffset = 305 + lighthouse.seenBy.size * 58,
        ))
    }

    private fun createMapObject(
        pos: Position,
        atlasName: String,
        atlasIndex: Int? = null,
        xOffset: Int = 0,
        yOffset: Int = 0,
    ) = TiledMapTileMapObject(
        StaticTiledMapTile(atlasIndex?.let { tilesAtlas.findRegion(atlasName, it) } ?: tilesAtlas.findRegion(atlasName)),
        false,
        false,
    ).apply {
        x = (pos % mapWidth * HALF_TILE_WIDTH) + (pos / mapWidth * HALF_TILE_WIDTH) + xOffset
        y = (pos / mapWidth * HALF_TILE_HEIGHT) - (pos % mapWidth * HALF_TILE_HEIGHT) + yOffset
    }
}
