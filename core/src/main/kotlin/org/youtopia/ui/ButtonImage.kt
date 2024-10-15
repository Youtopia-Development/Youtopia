package org.youtopia.ui

import com.badlogic.gdx.scenes.scene2d.ui.Image
import org.youtopia.utils.tilesAtlas
import kotlin.math.min

class ButtonImage(asset: String) : Image(tilesAtlas.findRegion(asset)) {
    private val scale = min(50f / super.getPrefWidth(), 50f / super.getPrefHeight())
    override fun getPrefWidth(): Float = super.getPrefWidth() * scale
    override fun getPrefHeight(): Float = super.getPrefHeight() * scale
}
