package org.youtopia.map

import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import com.badlogic.gdx.utils.Array

class InterruptableAnimatedTiledMapTile(
    interval: Float,
    frameTiles: Array<StaticTiledMapTile?>?,
) : AnimatedTiledMapTile(interval, frameTiles) {
    override fun getCurrentFrameIndex(): Int = if (interrupted) 0 else super.getCurrentFrameIndex()

    companion object {
        var interrupted = false
    }
}
