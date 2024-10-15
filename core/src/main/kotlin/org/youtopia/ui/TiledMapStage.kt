package org.youtopia.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.Viewport
import kotlinx.coroutines.flow.MutableStateFlow
import ktx.scene2d.container
import ktx.scene2d.horizontalGroup
import ktx.scene2d.image
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import org.youtopia.map.HALF_TILE_HEIGHT
import org.youtopia.map.HALF_TILE_WIDTH
import org.youtopia.map.SelectableTiledMap
import org.youtopia.map.TILE_HEIGHT_INT
import org.youtopia.map.TILE_WIDTH_INT
import org.youtopia.map.TiledMapActor
import org.youtopia.map.TiledMapClickListener
import org.youtopia.data.Game
import org.youtopia.data.Position
import org.youtopia.utils.Ref
import org.youtopia.utils.mapUiAtlas

internal class TiledMapStage(
    cityLabels: MutableMap<Position, CityLabel>,
    game: Game,
    viewport: Viewport,
    batch: Batch,
    tiledMap: SelectableTiledMap,
    shouldHandleInput: Ref<Boolean>,
) : Stage(viewport, batch) {

    init {
        createActorsForLayer(tiledMap, shouldHandleInput, game)
        createCityLabels(game, cityLabels)
    }

    private fun createActorsForLayer(tiledMap: SelectableTiledMap, shouldHandleInput: Ref<Boolean>, game: Game) {
        for (x in 0 until game.size.first) {
            for (y in 0 until game.size.first) {
                val cell = tiledMap.getTilesLayerCell(x + y * game.size.first)
                val actor = TiledMapActor(tiledMap, cell)
                val halfTileWidth = TILE_WIDTH_INT / 2
                val halfTileHeight = TILE_HEIGHT_INT / 2
                actor.setBounds(
                    (x * halfTileWidth) + (y * halfTileWidth).toFloat() + 120,
                    (y * halfTileHeight) - (x * halfTileHeight).toFloat() + 178,
                    TILE_WIDTH_INT.toFloat(),
                    TILE_HEIGHT_INT.toFloat(),
                )
                addActor(actor)
                val eventListener: EventListener = TiledMapClickListener(actor, shouldHandleInput)
                actor.addListener(eventListener)
            }
        }
    }

    private fun createCityLabels(game: Game, cityLabels: MutableMap<Position, CityLabel>) {
        var cityTable: Table
        var incomeLabel: Label
        var populationGroup: HorizontalGroup
        var labelGroup: HorizontalGroup

        game.players.forEach {
            it.cities.forEach { (pos, city) ->
                addActor(scene2d.table {
                    x = (pos % game.size.first * HALF_TILE_WIDTH) + (pos / game.size.first * HALF_TILE_WIDTH) + HALF_TILE_WIDTH * 1.47f
                    y = (pos / game.size.first * HALF_TILE_HEIGHT) - (pos % game.size.first * HALF_TILE_HEIGHT) + HALF_TILE_HEIGHT * 1.7f
                    cityTable = table {
                        table {
                            padLeft(20f)
                            padRight(20f)
                            background(TextureRegionDrawable(mapUiAtlas.findRegion("blue_background")))
                            labelGroup = horizontalGroup {
                                space(10f)
                                if (city.isCapital) image(mapUiAtlas.findRegion("capital"))
                                label(style = "city_name", text = city.name).setFontScale(3f)
                                image(mapUiAtlas.findRegion("star_small"))
                                incomeLabel = label(style = "city_name", text = city.income.toString()) {
                                    setFontScale(3f)
                                }
                            }.apply { isTransform = false }
                        }.isTransform = false
                        row()
                        container{
                            minHeight(5f)
                        }.isTransform = false
                        row()
                        table {
                            populationGroup = horizontalGroup {
                                space(5f)
                                container {
                                    background = TextureRegionDrawable(mapUiAtlas.findRegion("empty_population"))
                                }.isTransform = false
                                container {
                                    background = TextureRegionDrawable(mapUiAtlas.findRegion("empty_population"))
                                }.isTransform = false
                            }.apply { isTransform = false }
                        }.isTransform = false
                    }.apply { isTransform = false }
                }.apply { isTransform = false })

                cityLabels[pos] = CityLabel(
                    cityTable,
                    incomeLabel,
                    labelGroup,
                    populationGroup,
                    MutableStateFlow(city.population),
                    MutableStateFlow(city.targetPopulation),
                )
            }
        }
    }
}
