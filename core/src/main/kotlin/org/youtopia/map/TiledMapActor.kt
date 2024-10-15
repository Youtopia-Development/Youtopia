package org.youtopia.map

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable

class TiledMapActor(
    val tiledMap: SelectableTiledMap,
    val cell: GameCell,
) : Actor() {
    override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {
        if (touchable && this.touchable != Touchable.enabled) return null
        if (!isVisible) return null
        return if (
            x * height / width + y > height / 2 &&
            x * height / width + y < height * 1.5f &&
            y - x * height / width < height / 2 &&
            y - x * height / width > -height / 2
        ) this else null
    }
}
