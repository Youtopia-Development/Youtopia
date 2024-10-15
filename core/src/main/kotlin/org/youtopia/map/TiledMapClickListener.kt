package org.youtopia.map

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import org.youtopia.utils.Ref

class TiledMapClickListener(
    private val actor: TiledMapActor,
    private val shouldHandleInput: Ref<Boolean>,
) : ClickListener() {
    override fun clicked(event: InputEvent, x: Float, y: Float) {
        if (!shouldHandleInput.value) return
        actor.tiledMap.select(actor.cell.x, actor.cell.y)
    }
}
