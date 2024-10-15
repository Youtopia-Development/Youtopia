package org.youtopia.map

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.maps.objects.TextureMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import kotlin.math.sqrt

class IsometricMapRenderer(
    map: TiledMap,
    batch: Batch,
) : BatchTiledMapRenderer(map, batch) {
    private val invIsoTransform by lazy {
        Matrix4().apply {
            idt()
            scale((sqrt(2.0) / 2.0).toFloat(), (sqrt(2.0) / 2.0 / TILE_WIDTH * TILE_HEIGHT).toFloat(), 1.0f)
            rotate(0.0f, 0.0f, 1.0f, -45f)
            inv()
        }
    }

    private val screenPos = Vector3()

    private val topRight = Vector2()
    private val bottomLeft = Vector2()
    private val topLeft = Vector2()
    private val bottomRight = Vector2()

    private fun translateScreenToIso(vec: Vector2): Vector3 {
        screenPos[vec.x, vec.y] = 0f
        screenPos.mul(invIsoTransform)

        return screenPos
    }

    @Suppress("CyclomaticComplexMethod", "NestedBlockDepth")
    override fun renderTileLayer(layer: TiledMapTileLayer) {
        val tileWidth = layer.tileWidth
        val tileHeight = layer.tileHeight

        val halfTileWidth = tileWidth * 0.5f
        val halfTileHeight = tileHeight * 0.5f

        // setting up the screen points
        // COL1
        topRight[viewBounds.x + viewBounds.width] = viewBounds.y
        // COL2
        bottomLeft[viewBounds.x] = viewBounds.y + viewBounds.height
        // ROW1
        topLeft[viewBounds.x] = viewBounds.y
        // ROW2
        bottomRight[viewBounds.x + viewBounds.width] = viewBounds.y + viewBounds.height

        // transforming screen coordinates to iso coordinates
        val row1 = (translateScreenToIso(topLeft).y / tileWidth).toInt() - 2
        val row2 = (translateScreenToIso(bottomRight).y / tileWidth).toInt() + 2

        val col1 = (translateScreenToIso(bottomLeft).x / tileWidth).toInt() - 2
        val col2 = (translateScreenToIso(topRight).x / tileWidth).toInt() + 2

        val rowMiddle = (translateScreenToIso(bottomLeft).y / tileWidth).toInt()

        for (row in row2 downTo row1) {
            for (col in col1..col2) {
                if ((row - row1 + col - col1 + 1 < rowMiddle - row1) ||
                    (row2 - row + col2 - col - 1 < rowMiddle - row1) ||
                    (row2 - row + col - col1 - 2 < row2 - rowMiddle) ||
                    (row - row1 + col2 - col + 2 < row2 - rowMiddle)) continue

                val x = (col * halfTileWidth) + (row * halfTileWidth)
                val y = (row * halfTileHeight) - (col * halfTileHeight)

                val cell = layer.getCell(col, row) as? GameCell ?: continue
                val tile = cell.tile

                tile?.apply {
                    val region = textureRegion as AtlasRegion

                    val x1 = x + offsetX + region.offsetX
                    val y1 = y + offsetY + region.offsetY

                    batch.draw(region, x1, y1, region.regionWidth.toFloat(), region.regionHeight.toFloat())
                }

                cell.topLeftBorderObject?.let { renderObject(it) }
                cell.topRightBorderObject?.let { renderObject(it) }
                for (obj in cell.roadObjects) {
                    obj?.let {renderObject(it) }
                }
                cell.terrainObject?.let { renderObject(it) }
                cell.resourceObject?.let { renderObject(it) }
                cell.buildingObject?.let { renderObject(it) }
                for (obj in cell.buildingPartObjects) {
                    renderObject(obj)
                }
                cell.bottomLeftBorderObject?.let { renderObject(it) }
                cell.bottomRightBorderObject?.let { renderObject(it) }
            }
        }
    }

    private fun renderObject(obj: TextureMapObject) {
        val x: Float = obj.x
        val y: Float = obj.y

        val color = obj.properties.get("color", Color::class.java)

        val changeColor = color != null
        var flipHorizontally = false
        var flipVertically = false

        var textureRegion = obj.textureRegion

        if (obj is TiledMapTileMapObject) {
            if (obj.isFlipHorizontally) flipHorizontally = true
            if (obj.isFlipVertically) flipVertically = true
            textureRegion = obj.tile.textureRegion
        }

        val width = textureRegion.regionWidth.toFloat()
        val height = textureRegion.regionHeight.toFloat()

        textureRegion.flip(flipHorizontally, flipVertically)

        if (changeColor) batch.color = color
        batch.draw(
            textureRegion,
            x + if (textureRegion is AtlasRegion) textureRegion.offsetX else 0f,
            y + if (textureRegion is AtlasRegion) textureRegion.offsetY else 0f,
            width,
            height,
        )
        if (changeColor) batch.color = Color.WHITE

        textureRegion.flip(flipHorizontally, flipVertically)
    }
}
