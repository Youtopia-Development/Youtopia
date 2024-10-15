package org.youtopia.effects

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.suspendCancellableCoroutine
import ktx.actors.onClick
import ktx.scene2d.KHorizontalGroup
import ktx.scene2d.container
import ktx.scene2d.horizontalGroup
import ktx.scene2d.imageButton
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.verticalGroup
import org.youtopia.data.GameEffect
import org.youtopia.map.SelectableTiledMap
import org.youtopia.server.zipline.chooseCityUpgradeReward
import org.youtopia.ui.ButtonImage
import org.youtopia.ui.CityLabel
import org.youtopia.ui.UiStage
import org.youtopia.utils.image
import org.youtopia.utils.mapUiAtlas
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

suspend fun upgradeCity(
    action: GameEffect.UpgradeCity,
    cityLabels: MutableMap<Int, CityLabel>,
    gameEffectQueue: MutableSharedFlow<GameEffect>,
    tileMap: SelectableTiledMap,
    uiStage: UiStage,
    score: MutableStateFlow<Int>,
    stars: MutableStateFlow<Int>,
    income: MutableStateFlow<Int>,
) {
    val cityLabel = cityLabels[action.cell] ?: error("City not found at position ${action.cell}")
    val populationGroup = cityLabel.populationGroup

    populationGroup.clearChildren()
    cityLabel.population.value = 0
    cityLabel.targetPopulation.value = action.targetPopulation
    repeat(cityLabel.targetPopulation.value) {
        populationGroup.addActor(scene2d.container {
            background = TextureRegionDrawable(mapUiAtlas.findRegion("empty_population"))
        })
    }
    cityLabel.incomeLabel.setText(cityLabel.incomeLabel.text.toString().toInt() + 1)

    val gameCell = tileMap.getTilesLayerCell(action.cell)
    gameCell.addCity(action.cell, action.city)

    income.update { it + 1 }
    uiStage.starsLabel.setText("${stars.value} (+${income.value})")

    score.update { it + action.score }
    uiStage.scoreLabel.setText(score.value)

    openCityUpgradeUi(action, gameEffectQueue, uiStage)
}

private suspend fun openCityUpgradeUi(
    action: GameEffect.UpgradeCity,
    gameEffectQueue: MutableSharedFlow<GameEffect>,
    uiStage: UiStage,
) {
    suspendCancellableCoroutine { continuation ->
        uiStage.cityUpgradePopUp = scene2d.table {
            setFillParent(true)
            center()
            table {
                background(TextureRegionDrawable(Texture(Pixmap(1, 1, Pixmap.Format.RGBA8888).apply {
                    setColor(0f, 0f, 0f, 1f)
                    fill()
                })))
                pad(10f)
                verticalGroup {
                    label("${action.city.name} level up!")
                    space(10f)
                    horizontalGroup {
                        upgradeButton(action, gameEffectQueue, uiStage, continuation, index = 0)
                        space(10f)
                        upgradeButton(action, gameEffectQueue, uiStage, continuation, index = 1)
                    }
                }
            }
        }.also { uiStage.addActor(it) }
    }
}

private fun KHorizontalGroup.upgradeButton(
    action: GameEffect.UpgradeCity,
    gameEffectQueue: MutableSharedFlow<GameEffect>,
    uiStage: UiStage,
    continuation: Continuation<Unit>,
    index: Int,
) = addActor(scene2d.table {
    imageButton("button_available") {
        onClick {
            chooseCityUpgradeReward(action.cell, action.upgradeRewards[index]).forEach { gameEffect ->
                gameEffectQueue.tryEmit(gameEffect)
            }
            uiStage.cityUpgradePopUp?.remove()
            continuation.resume(Unit)
        }
    }.add(ButtonImage(action.upgradeRewards[index].image()))
    row()
    container {
        prefWidth(90f)
        prefHeight(50f)
        center()
        label(action.upgradeRewards[index].printedName) {
            wrap = true
            setAlignment(Align.top, Align.center)
        }
    }
})
