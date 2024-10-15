package org.youtopia.effects

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import ktx.scene2d.container
import ktx.scene2d.scene2d
import org.youtopia.data.GameEffect
import org.youtopia.ui.CityLabel
import org.youtopia.ui.UiStage
import org.youtopia.utils.mapUiAtlas

fun addPopulation(
    action: GameEffect.AddPopulation,
    cityLabels: MutableMap<Int, CityLabel>,
    uiStage: UiStage,
    score: MutableStateFlow<Int>,
    stars: MutableStateFlow<Int>,
    income: MutableStateFlow<Int>,
) {
    val cityLabel = cityLabels[action.to] ?: error("City not found at position ${action.to}")
    val populationGroup = cityLabel.populationGroup

    if (cityLabel.population.value < 0) {
        income.update { it + 1 }
        uiStage.starsLabel.setText("${stars.value} (+${income.value})")

        val incomeLabel = cityLabel.incomeLabel
        incomeLabel.setText(incomeLabel.text.toString().toInt() + 1)
    }

    score.update { it + 5 }
    uiStage.scoreLabel.setText(score.value)

    populationGroup.clearChildren()
    cityLabel.population.update { it + 1 }

    if (cityLabel.population.value > 0) {
        repeat(cityLabel.population.value) {
            populationGroup.addActor(scene2d.container {
                background = TextureRegionDrawable(mapUiAtlas.findRegion("filled_population"))
            })
        }
        repeat(cityLabel.targetPopulation.value - cityLabel.population.value) {
            populationGroup.addActor(scene2d.container {
                background = TextureRegionDrawable(mapUiAtlas.findRegion("empty_population"))
            })
        }
    } else {
        repeat(-cityLabel.population.value) {
            populationGroup.addActor(scene2d.container {
                background = TextureRegionDrawable(mapUiAtlas.findRegion("missing_population"))
            })
        }
        repeat(cityLabel.targetPopulation.value + cityLabel.population.value) {
            populationGroup.addActor(scene2d.container {
                background = TextureRegionDrawable(mapUiAtlas.findRegion("empty_population"))
            })
        }
    }
}
