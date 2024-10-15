package org.youtopia.map

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import kotlinx.coroutines.flow.MutableSharedFlow
import ktx.actors.onClick
import ktx.scene2d.container
import ktx.scene2d.horizontalGroup
import ktx.scene2d.imageButton
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import org.youtopia.data.Building
import org.youtopia.data.GameEffect
import org.youtopia.data.Position
import org.youtopia.data.Terrain
import org.youtopia.data.Tile
import org.youtopia.data.TileActionAvailability
import org.youtopia.server.zipline.performTileAction
import org.youtopia.server.zipline.requestAvailableTileActions
import org.youtopia.ui.ButtonImage
import org.youtopia.utils.Ref
import org.youtopia.utils.getResourceAtlasName
import org.youtopia.utils.getTerrainAtlasName
import org.youtopia.utils.image
import org.youtopia.utils.tilesAtlas

class SelectableTiledMap(
    gameTiles: List<Tile>,
    private val gameSize: Pair<Int, Int>,
    private val gameEffectQueue: MutableSharedFlow<GameEffect>,
    private val bottomButtons: Table,
    private val shouldHandleInput: Ref<Boolean>,
) : TiledMap() {

    private var selectedTile: Pair<Int, Int>? = null

    private val tilesLayer get() = layers[TILES_LAYER_NAME] as TiledMapTileLayer
    private val selectionLayer get() = layers[SELECTION_LAYER_NAME] as TiledMapTileLayer

    init {
        addTilesLayer(gameTiles)
        addSelectionLayer()
    }

    @Suppress("LongMethod", "CyclomaticComplexMethod")
    private fun addTilesLayer(gameTiles: List<Tile>) {
        val cityPositions = mutableListOf<Position>()
        val tilesLayer = TiledMapTileLayer(gameSize.first, gameSize.second, TILE_WIDTH_INT, TILE_HEIGHT_INT)
        tilesLayer.name = TILES_LAYER_NAME
        layers.add(tilesLayer)
        for (position in 0..<gameSize.first * gameSize.second) {
            val x = position % gameSize.first
            val y = position / gameSize.first
            val tile = gameTiles[position]
            val cell = GameCell(
                StaticTiledMapTile(tilesAtlas.findRegion(getTerrainAtlasName(tile.terrain, tile.tribe, position, gameSize))),
                x,
                y,
                mapWidth = gameSize.first,
            )
            tilesLayer.setCell(x, y, cell)

            when (tile.terrain) {
                Terrain.FOREST -> {
                    cell.addTerrain(position, "forest_${tile.tribe.name.lowercase()}")
                }
                Terrain.MOUNTAIN -> {
                    cell.addTerrain(position, "mountain_${tile.tribe.name.lowercase()}")
                }
                else -> Unit
            }

            tile.resource?.let {
                cell.addResource(position, getResourceAtlasName(it, tile.tribe))
            }

            tile.building?.let {
                when (it) {
                    is Building.City -> {
                        cell.addCity(position, it)
                        cell.addPath(position, 8, Path.ROAD)
                        cell.isRoad = true
                        cityPositions.add(position)
                    }

                    is Building.Market -> {
                        cell.addMarket(position, it)
                    }

                    is Building.Lighthouse -> {
                        cell.addLighthouse(position, it)
                    }

                    else -> {
                        cell.addBuilding(position, it)
                    }
                }
            }

            tile.owner?.let { owner ->
                if (x == 0 || gameTiles[position - 1].owner != owner) {
                    cell.addBorderMapObject(position, BorderDirection.TOP_LEFT)
                }
                if ((position + 1) % gameSize.first == 0 || gameTiles[position + 1].owner != owner) {
                    cell.addBorderMapObject(position, BorderDirection.BOTTOM_RIGHT)
                }
                if (y == 0 || gameTiles[position - gameSize.first].owner != owner) {
                    cell.addBorderMapObject(position, BorderDirection.BOTTOM_LEFT)
                }
                if (y == gameSize.second - 1 || gameTiles[position + gameSize.first].owner != owner) {
                    cell.addBorderMapObject(position, BorderDirection.TOP_RIGHT)
                }
            }
        }

        cityPositions.forEach {
            for (neighbour in neighbours(it, gameSize) + it) {
                getTilesLayerCell(neighbour).apply {
                    city = it
                    owner = 0
                }
            }
        }
    }

    private fun addSelectionLayer() {
        val selectionLayer = TiledMapTileLayer(gameSize.first, gameSize.second, TILE_WIDTH_INT, TILE_HEIGHT_INT)
        selectionLayer.name = SELECTION_LAYER_NAME
        layers.add(selectionLayer)
    }

    @Suppress("CyclomaticComplexMethod")
    fun select(x: Int, y: Int) {
        selectedTile?.let {
            selectionLayer.setCell(it.first, it.second, null)
            if (selectedTile == x to y) {
                selectedTile = null
                bottomButtons.clearChildren()
                return
            }
        }
        val pos = x + y * gameSize.first
        val cell = GameCell(tile = StaticTiledMapTile(tilesAtlas.findRegion("selection_active")), x, y, gameSize.first)
        val borderTile = StaticTiledMapTile(tilesAtlas.findRegion("city_border"))
        val borderTileFlipped = StaticTiledMapTile(TextureRegion(tilesAtlas.findRegion("city_border")).apply {
            flip(true, false)
        })
        val borderColor = Color.valueOf("0199FE")
        if (getTilesLayerCell(x + y * gameSize.first).isCity) {
            for (territory in round(pos, 2, gameSize)) {
                if (getTilesLayerCell(territory).city != pos) continue
                val xOffset = (territory % gameSize.first * HALF_TILE_WIDTH) + (territory / gameSize.first * HALF_TILE_WIDTH)
                val yOffset = (territory / gameSize.first * HALF_TILE_HEIGHT) - (territory % gameSize.first * HALF_TILE_HEIGHT)
                if (territory % gameSize.first == 0 || getTilesLayerCell(territory - 1).city != pos) {
                    cell.buildingPartObjects.add(TiledMapTileMapObject(borderTile, false, false).apply {
                        this.x = xOffset + 120
                        this.y = yOffset + 328
                        properties.put("color", borderColor)
                    })
                }
                if (territory % gameSize.first == gameSize.first - 1 || getTilesLayerCell(territory + 1).city != pos) {
                    cell.buildingPartObjects.add(TiledMapTileMapObject(borderTile, false, false).apply {
                        this.x = xOffset + 120 + HALF_TILE_WIDTH
                        this.y = yOffset + 174
                        properties.put("color", borderColor)
                    })
                }
                if (territory / gameSize.first == 0 || getTilesLayerCell(territory - gameSize.first).city != pos) {
                    // flip doesn't work here for some reason :/
                    cell.buildingPartObjects.add(TiledMapTileMapObject(borderTileFlipped, false, false).apply {
                        this.x = xOffset + 120
                        this.y = yOffset + 144 + 30
                        properties.put("color", borderColor)
                    })
                }
                if (territory / gameSize.first == gameSize.second - 1 || getTilesLayerCell(territory + gameSize.first).city != pos) {
                    cell.buildingPartObjects.add(TiledMapTileMapObject(borderTileFlipped, false, false).apply {
                        this.x = xOffset + 120 + HALF_TILE_WIDTH
                        this.y = yOffset + 298 + 30
                        properties.put("color", borderColor)
                    })
                }
            }
        }
        selectionLayer.setCell(x, y, cell)
        bottomButtons.clearChildren()
        bottomButtons.add(bottomSheet(pos))

        selectedTile = x to y
    }

    fun deselect() {
        selectedTile?.let {
            selectionLayer.setCell(it.first, it.second, null)
        }
        selectedTile = null
        bottomButtons.clearChildren()
    }

    fun getTilesLayerCell(pos: Position) = tilesLayer.getCell(pos % gameSize.first, pos / gameSize.first) as GameCell

    private fun bottomSheet(
        pos: Position,
    ) = scene2d.horizontalGroup {
        requestAvailableTileActions(pos).forEach { action ->
            table {
                horizontalGroup {
                    padLeft(10f)
                    padRight(10f)
                    when (action.availability) {
                        TileActionAvailability.AVAILABLE -> imageButton("button_available") {
                            onClick {
                                if (!shouldHandleInput.value) return@onClick
                                selectedTile?.let {
                                    selectionLayer.setCell(
                                        it.first,
                                        it.second,
                                        null,
                                    )
                                    selectedTile = null
                                    bottomButtons.clearChildren()
                                    performTileAction(
                                        it.first + it.second * gameSize.first,
                                        action,
                                    ).forEach { gameEffect ->
                                        gameEffectQueue.tryEmit(gameEffect)
                                    }
                                }
                            }
                        }.apply { add(ButtonImage(action.image())) }
                        TileActionAvailability.NOT_ENOUGH_RESOURCES -> imageButton("button_expensive")
                            .apply { add(ButtonImage(action.image())) }
                        TileActionAvailability.NOT_RESEARCHED -> TODO()
                    }
                }
                row()
                container {
                    prefWidth(90f)
                    prefHeight(50f)
                    center()
                    label(action.name) {
                        wrap = true
                        setAlignment(Align.top, Align.center)
                    }
                }
            }
        }
    }

    companion object {
        private const val TILES_LAYER_NAME = "tiles"
        private const val SELECTION_LAYER_NAME = "selection"

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

        private fun neighbours(position: Position, size: Pair<Int, Int>): List<Position> = buildList {
            val notFirstColumn = position % size.first > 0
            val notLastColumn = position % size.first != size.first - 1
            val notFirstRow = position / size.first != 0
            val notLastRow = position / size.first != size.second - 1
            if (notFirstColumn) add(position - 1)
            if (notLastColumn) add(position + 1)
            if (notFirstRow) add(position - size.first)
            if (notLastRow) add(position + size.first)
            if (notFirstRow && notFirstColumn) add(position - size.first - 1)
            if (notFirstRow && notLastColumn) add(position - size.first + 1)
            if (notLastRow && notFirstColumn) add(position + size.first - 1)
            if (notLastRow && notLastColumn) add(position + size.first + 1)
        }
    }
}
