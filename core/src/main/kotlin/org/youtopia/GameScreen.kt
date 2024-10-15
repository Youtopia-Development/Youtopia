package org.youtopia

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.TiledMapRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.async.KtxAsync
import ktx.graphics.use
import ktx.scene2d.Scene2DSkin
import org.youtopia.effects.renderGameEffect
import org.youtopia.input.CameraController
import org.youtopia.input.CombiningInputMultiplexer
import org.youtopia.map.CULLING_BORDER
import org.youtopia.map.HALF_TILE_HEIGHT
import org.youtopia.map.HALF_TILE_WIDTH
import org.youtopia.map.IsometricMapRenderer
import org.youtopia.map.SelectableTiledMap
import org.youtopia.data.GameEffect
import org.youtopia.data.Position
import org.youtopia.server.zipline.generateGame
import org.youtopia.ui.CityLabel
import org.youtopia.ui.TiledMapStage
import org.youtopia.ui.UiStage
import org.youtopia.ui.bottomButtons
import org.youtopia.ui.defaultSkin
import org.youtopia.utils.Ref
import kotlin.math.abs

internal class GameScreen : KtxScreen {

    private val game = generateGame(50 to 50)

    private val gameEffectQueue = MutableSharedFlow<GameEffect>(
        extraBufferCapacity = Int.MAX_VALUE,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private val batch by lazy { SpriteBatch() }

    private val camera by lazy { OrthographicCamera() }

    private val viewport by lazy { ScalingViewport(
        Scaling.fit,
        Gdx.graphics.width.toFloat(),
        Gdx.graphics.height.toFloat(),
        camera,
    ) }

    private val uiViewport by lazy { ScreenViewport() }

    private val score by lazy { MutableStateFlow(game.players[0].score) }
    private val stars by lazy { MutableStateFlow(game.players[0].stars) }
    private val income by lazy { MutableStateFlow(game.players[0].income) }
    private val turn by lazy { MutableStateFlow(game.turn) }

    private val tileMap: SelectableTiledMap by lazy { SelectableTiledMap(
        game.tiles,
        game.size,
        gameEffectQueue,
        bottomButtons,
        shouldHandleInput,
    ) }

    private val renderer: TiledMapRenderer by lazy { IsometricMapRenderer(tileMap, batch) }

    private val font by lazy { BitmapFont() }

    private val cityLabels by lazy { mutableMapOf<Position, CityLabel>() }

    private val mapStage: Stage by lazy { TiledMapStage(cityLabels, game, viewport, batch, tileMap, shouldHandleInput) }

    private val uiStage: UiStage by lazy { UiStage(uiViewport, batch, gameEffectQueue, shouldHandleInput, tileMap, bottomButtons) }

    private val shapeRenderer: ShapeRenderer by lazy { ShapeRenderer() }

    private val cameraController: CameraController by lazy { CameraController(camera) }

    private val inputProcessor by lazy {
        InputMultiplexer(
            uiStage,
            CombiningInputMultiplexer(
                mapStage,
                cameraController,
            ),
        )
    }

    private val shouldHandleInput = Ref(true)

    override fun show() {
        KtxAsync.initiate()

        listenForGameEffects()

        Scene2DSkin.defaultSkin = defaultSkin

        Gdx.input.inputProcessor = inputProcessor

        val capital = game.players.first().cities.entries.first { it.value.isCapital }.key
        camera.position.set(
            (capital % game.size.first + capital / game.size.first) * HALF_TILE_WIDTH + 375,
            (-capital % game.size.first + capital / game.size.first) * HALF_TILE_HEIGHT + 500,
            0f,
        )
        camera.zoom = 3f

        uiStage.scoreLabel.setText(score.value)
        uiStage.starsLabel.setText("${stars.value} (+${income.value})")
        uiStage.turnLabel.setText(game.turn)
    }

    override fun render(delta: Float) {
        viewport.apply()

        renderer.setView(camera)
        renderer.render()

        if (cameraController.isInteractionOn) {
            val width = camera.viewportWidth * camera.zoom
            val height = camera.viewportHeight * camera.zoom
            val w = (width * abs(camera.up.y.toDouble()) + height * abs(camera.up.x.toDouble())).toFloat()
            val h = (height * abs(camera.up.y.toDouble()) + width * abs(camera.up.x.toDouble())).toFloat()
            mapStage.root.cullingArea = Rectangle(
                camera.position.x - w / 2 - CULLING_BORDER,
                camera.position.y - h / 2 - CULLING_BORDER,
                w + CULLING_BORDER * 2,
                h + CULLING_BORDER * 2,
            )
            mapStage.draw()
        }

        drawGradientBorders()

        uiViewport.apply()

        uiStage.act(delta)
        uiStage.draw()

        batch.use {
            font.draw(batch, "FPS: " + Gdx.graphics.framesPerSecond, 10f, 20f)
        }
    }

    override fun dispose() {
        batch.disposeSafely()
    }

    override fun resize(width: Int, height: Int) {
        uiViewport.update(width, height, true)
    }

    private fun listenForGameEffects() = KtxAsync.launch {
        gameEffectQueue.collect { gameEffect ->
            shouldHandleInput.value = false
            renderGameEffect(gameEffect, game.size, gameEffectQueue, tileMap, uiStage, score, stars, income, turn, cityLabels)
            shouldHandleInput.value = true
        }
    }

    private fun drawGradientBorders() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.rect(
            0f,
            Gdx.graphics.height.toFloat() - 100f,
            Gdx.graphics.width.toFloat(),
            100f,
            Color.CLEAR,
            Color.CLEAR,
            Color.BLACK,
            Color.BLACK,
        )
        shapeRenderer.rect(
            0f,
            0f,
            Gdx.graphics.width.toFloat(),
            100f,
            Color.BLACK,
            Color.BLACK,
            Color.CLEAR,
            Color.CLEAR,
        )
        shapeRenderer.end()
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }
}
