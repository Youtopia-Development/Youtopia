package org.youtopia.input

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor

@Suppress("UNCHECKED_CAST")
class CombiningInputMultiplexer(vararg inputProcessors: InputProcessor) : InputMultiplexer() {

    init {
        inputProcessors.forEach { addProcessor(it) }
    }

    override fun keyDown(keycode: Int): Boolean {
        val items: Array<Any> = processors.begin() as Array<Any>
        var handled = false
        try {
            for (i in 0..<processors.size) {
                val item = items[i] as InputProcessor
                handled = item.keyDown(keycode) || handled
            }
        } finally {
            processors.end()
        }
        return handled
    }

    override fun keyUp(keycode: Int): Boolean {
        val items: Array<Any> = processors.begin() as Array<Any>
        var handled = false
        try {
            for (i in 0..<processors.size) {
                val item = items[i] as InputProcessor
                handled = item.keyUp(keycode) || handled
            }
        } finally {
            processors.end()
        }
        return handled
    }

    override fun keyTyped(character: Char): Boolean {
        val items: Array<Any> = processors.begin() as Array<Any>
        var handled = false
        try {
            for (i in 0..<processors.size) {
                val item = items[i] as InputProcessor
                handled = item.keyTyped(character) || handled
            }
        } finally {
            processors.end()
        }
        return handled
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val items: Array<Any> = processors.begin() as Array<Any>
        var handled = false
        try {
            for (i in 0..<processors.size) {
                val item = items[i] as InputProcessor
                handled = item.touchDown(screenX, screenY, pointer, button) || handled
            }
        } finally {
            processors.end()
        }
        return handled
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val items: Array<Any> = processors.begin() as Array<Any>
        var handled = false
        try {
            for (i in 0..<processors.size) {
                val item = items[i] as InputProcessor
                handled = item.touchUp(screenX, screenY, pointer, button) || handled
            }
        } finally {
            processors.end()
        }
        return handled
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val items: Array<Any> = processors.begin() as Array<Any>
        var handled = false
        try {
            for (i in 0..<processors.size) {
                val item = items[i] as InputProcessor
                handled = item.touchCancelled(screenX, screenY, pointer, button) || handled
            }
        } finally {
            processors.end()
        }
        return handled
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val items: Array<Any> = processors.begin() as Array<Any>
        var handled = false
        try {
            for (i in 0..<processors.size) {
                val item = items[i] as InputProcessor
                handled = item.touchDragged(screenX, screenY, pointer) || handled
            }
        } finally {
            processors.end()
        }
        return handled
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        val items: Array<Any> = processors.begin() as Array<Any>
        var handled = false
        try {
            for (i in 0..<processors.size) {
                val item = items[i] as InputProcessor
                handled = item.mouseMoved(screenX, screenY) || handled
            }
        } finally {
            processors.end()
        }
        return handled
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        val items: Array<Any> = processors.begin() as Array<Any>
        var handled = false
        try {
            for (i in 0..<processors.size) {
                val item = items[i] as InputProcessor
                handled = item.scrolled(amountX, amountY) || handled
            }
        } finally {
            processors.end()
        }
        return handled
    }
}
