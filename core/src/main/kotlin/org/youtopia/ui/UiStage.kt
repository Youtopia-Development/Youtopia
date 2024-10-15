package org.youtopia.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.Viewport
import kotlinx.coroutines.flow.MutableSharedFlow
import ktx.actors.onClick
import ktx.scene2d.actors
import ktx.scene2d.container
import ktx.scene2d.horizontalGroup
import ktx.scene2d.imageButton
import ktx.scene2d.label
import ktx.scene2d.table
import org.youtopia.map.SelectableTiledMap
import org.youtopia.data.GameEffect
import org.youtopia.data.GeneralAction
import org.youtopia.server.zipline.performGeneralAction
import org.youtopia.utils.Ref

class UiStage(
    uiViewport: Viewport,
    batch: Batch,
    gameEffectQueue: MutableSharedFlow<GameEffect>,
    shouldHandleInput: Ref<Boolean>,
    tileMap: SelectableTiledMap,
    bottomButtons: Table,
) : Stage(uiViewport, batch) {
    val scoreLabel: Label
    val starsLabel: Label
    val turnLabel: Label

    var cityUpgradePopUp: Table? = null

    init {
        actors {
            table {
                setFillParent(true)
                top()

                horizontalGroup {
                    table {
                        label("Score")
                        row()
                        scoreLabel = label("")
                    }

                    space(100f)

                    table {
                        label("Stars")
                        row()
                        starsLabel = label("")
                    }

                    space(100f)

                    table {
                        label("Turn")
                        row()
                        turnLabel = label("0")
                    }
                }
            }

            addActor(bottomButtons)

            table {
                setFillParent(true)
                bottom()
                right()

                table {
                    horizontalGroup {
                        padLeft(10f)
                        padRight(10f)
                        imageButton("button_available") {
                            onClick {
                                if (!shouldHandleInput.value) return@onClick
                                tileMap.deselect()
                                performGeneralAction(GeneralAction.PassTurn).forEach { gameEffect ->
                                    gameEffectQueue.tryEmit(gameEffect)
                                }
                            }
                        }.add(ButtonImage("end_turn"))
                    }
                    row()
                    container {
                        prefWidth(90f)
                        prefHeight(50f)
                        center()
                        label("Pass Turn") {
                            wrap = true
                            setAlignment(Align.top, Align.center)
                        }
                    }
                }
            }
        }
    }
}
