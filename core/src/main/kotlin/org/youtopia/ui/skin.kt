package org.youtopia.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Image
import ktx.style.color
import ktx.style.imageButton
import ktx.style.label
import ktx.style.skin
import ktx.style.textButton
import org.youtopia.utils.mapUiAtlas
import org.youtopia.utils.tilesAtlas

val defaultSkin = skin {
    textButton {
        font = BitmapFont(Gdx.files.internal("fonts/josefin-sans-italic-16.fnt"))
        fontColor = Color.WHITE
    }
    label {
        font = BitmapFont(Gdx.files.internal("fonts/josefin-sans-italic-16.fnt"))
        fontColor = Color.WHITE
    }
    label(name = "city_name") { // Reusing the map UI texture to avoid expensive texture changes on the GPU
        font = BitmapFont(Gdx.files.internal("fonts/josefin-sans-italic-24.fnt"), mapUiAtlas.findRegion("font"))
        fontColor = Color.WHITE
    }
    imageButton(name = "button_available") {
        up = Image(tilesAtlas.findRegion("button_available")).drawable.apply {
            minWidth = 80f
            minHeight = 80f
        }
    }
    imageButton(name = "button_expensive") {
        up = Image(tilesAtlas.findRegion("button_expensive")).drawable.apply {
            minWidth = 80f
            minHeight = 80f
        }
    }
    skin {
        color("white", red = 1f, green = 1f, blue = 1f)
        color("black", red = 0f, green = 0f, blue = 0f)
        color("red", red = 1f, green = 0f, blue = 0f)
    }
}
