package org.youtopia.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import org.youtopia.map.CAMERA_UI_MIN_ZOOM
import org.youtopia.map.InterruptableAnimatedTiledMapTile
import org.youtopia.map.TILE_HEIGHT
import org.youtopia.map.TILE_WIDTH
import kotlin.math.max

// FIXME: rework this
internal class CameraController private constructor(
    gestureListener: CameraGestureListener,
    camera: OrthographicCamera,
) : GestureDetector(
    gestureListener
) {
    var isInteractionOn = true

    /** The angle to rotate when moved the full width or height of the screen.  */
    private val rotateAngle: Float = 360f

    /** The button for translating the camera along the up/right plane  */
    private val translateButton: Int = Buttons.LEFT

    /** The units to translate the camera when moved the full width or height of the screen.  */
    private val translateUnits: Float = 1500f // FIXME auto calculate this based on the target

    /** The button for translating the camera along the direction axis  */
    private val forwardButton: Int = Buttons.MIDDLE

    /** The key which must be pressed to activate rotate, translate and forward or 0 to always activate.  */
    private val activateKey: Int = Buttons.LEFT

    /** Indicates if the activateKey is currently being pressed.  */
    private var activatePressed: Boolean = false

    /** Whether scrolling requires the activeKey to be pressed (false) or always allow scrolling (true).  */
    private val alwaysScroll: Boolean = true

    /** The weight for each scrolled amount.  */
    private val scrollFactor: Float = -0.00001f

    /** World units per screen size  */
    private val pinchZoomFactor: Float = 10f

    /** Whether to update the camera after it has been changed.  */
    private val autoUpdate: Boolean = true

    /** The target to rotate around.  */
    private val target: Vector3 = Vector3()

    /** Whether to update the target on translation  */
    private val translateTarget: Boolean = true

    /** Whether to update the target on forward  */
    private val forwardTarget: Boolean = true

    private val forwardKey: Int = Input.Keys.W
    private var forwardPressed: Boolean = false
    private val backwardKey: Int = Input.Keys.S
    private var backwardPressed: Boolean = false
    private val rotateRightKey: Int = Input.Keys.A
    private var rotateRightPressed: Boolean = false
    private val rotateLeftKey: Int = Input.Keys.D
    private var rotateLeftPressed: Boolean = false

    /** The camera.  */
    private var camera: OrthographicCamera

    /** The current (first) button being pressed.  */
    private var button: Int = -1

    private var startX = 0f
    private var startY = 0f
    private val tmpV1 = Vector3()
    private val tmpV2 = Vector3()

    private class CameraGestureListener : GestureAdapter() {
        var controller: CameraController? = null
        private var previousZoom = 0f

        override fun touchDown(x: Float, y: Float, pointer: Int, button: Int): Boolean {
            previousZoom = 0f
            return false
        }

        override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean {
            return false
        }

        override fun longPress(x: Float, y: Float): Boolean {
            return false
        }

        override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean {
            return false
        }

        override fun pan(x: Float, y: Float, deltaX: Float, deltaY: Float): Boolean {
            return false
        }

        override fun zoom(initialDistance: Float, distance: Float): Boolean {
            val newZoom = distance - initialDistance
            val amount = newZoom - previousZoom
            previousZoom = newZoom
            val w = Gdx.graphics.width.toFloat()
            val h = Gdx.graphics.height.toFloat()
            return controller?.pinchZoom(amount / (if ((w > h)) h else w)) ?: error("Controller was not initialized.")
        }

        override fun pinch(
            initialPointer1: Vector2,
            initialPointer2: Vector2,
            pointer1: Vector2,
            pointer2: Vector2
        ): Boolean {
            return false
        }
    }

    constructor(camera: OrthographicCamera) : this(CameraGestureListener(), camera)

    fun update() {
        if (rotateRightPressed || rotateLeftPressed || forwardPressed || backwardPressed) {
            val delta = Gdx.graphics.deltaTime
            if (rotateRightPressed) camera.rotate(camera.up, -delta * rotateAngle)
            if (rotateLeftPressed) camera.rotate(camera.up, delta * rotateAngle)
            if (forwardPressed) {
                camera.translate(tmpV1.set(camera.direction).scl(delta * translateUnits))
                if (forwardTarget) target.add(tmpV1)
            }
            if (backwardPressed) {
                camera.translate(tmpV1.set(camera.direction).scl(-delta * translateUnits))
                if (forwardTarget) target.add(tmpV1)
            }
            if (autoUpdate) camera.update()
        }
    }

    private var touched = 0
    private var multiTouch = false

    init {
        gestureListener.controller = this
        this.camera = camera
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        touched = touched or (1 shl pointer)
        multiTouch = !MathUtils.isPowerOfTwo(touched)
        if (multiTouch) this.button = -1
        else if (this.button < 0 && (activateKey == 0 || activatePressed)) {
            startX = screenX.toFloat()
            startY = screenY.toFloat()
            this.button = button
        }
        return super.touchDown(screenX, screenY, pointer, button) || (activateKey == 0 || activatePressed)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        touched = touched and (-1 xor (1 shl pointer))
        multiTouch = !MathUtils.isPowerOfTwo(touched)
        if (button == this.button) this.button = -1
        return super.touchUp(screenX, screenY, pointer, button) || activatePressed
    }

    private fun process(deltaX: Float, deltaY: Float, button: Int): Boolean {
        if (button == translateButton) {
            camera.translate(tmpV1.set(Vector3.X).scl(-deltaX * translateUnits * camera.zoom))
            camera.translate(tmpV2.set(Vector3.Y).scl(-deltaY * translateUnits * TILE_HEIGHT / TILE_WIDTH * camera.zoom))
            if (translateTarget) target.add(tmpV1).add(tmpV2)
        } else if (button == forwardButton) {
            camera.translate(tmpV1.set(camera.direction).scl(deltaY * translateUnits))
            if (forwardTarget) target.add(tmpV1)
        }
        if (autoUpdate) camera.update()
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val result = super.touchDragged(screenX, screenY, pointer)
        if (result || this.button < 0) return result
        val deltaX = (screenX - startX) / Gdx.graphics.width
        val deltaY = (startY - screenY) / Gdx.graphics.height
        startX = screenX.toFloat()
        startY = screenY.toFloat()
        return process(deltaX, deltaY, button)
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return zoom(amountY * scrollFactor * translateUnits)
    }

    private fun zoom(amount: Float): Boolean {
        if (!alwaysScroll && activateKey != 0 && !activatePressed) return false
        camera.zoom -= amount
        camera.zoom = max(camera.zoom.toDouble(), 1.0).toFloat()
        if (camera.zoom > CAMERA_UI_MIN_ZOOM && isInteractionOn) {
            isInteractionOn = false
            InterruptableAnimatedTiledMapTile.interrupted = true
        } else if (camera.zoom < CAMERA_UI_MIN_ZOOM && !isInteractionOn) {
            isInteractionOn = true
            InterruptableAnimatedTiledMapTile.interrupted = false
        }
        if (autoUpdate) camera.update()
        return true
    }

    private fun pinchZoom(amount: Float): Boolean {
        return zoom(pinchZoomFactor * amount)
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == activateKey) activatePressed = true
        when (keycode) {
            forwardKey -> forwardPressed = true
            backwardKey -> backwardPressed = true
            rotateRightKey -> rotateRightPressed = true
            rotateLeftKey -> rotateLeftPressed = true
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        if (keycode == activateKey) {
            activatePressed = false
            button = -1
        }
        when (keycode) {
            forwardKey -> forwardPressed = false
            backwardKey -> backwardPressed = false
            rotateRightKey -> rotateRightPressed = false
            rotateLeftKey -> rotateLeftPressed = false
        }
        return false
    }
}
