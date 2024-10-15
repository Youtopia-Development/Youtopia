package org.youtopia.ui

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import kotlinx.coroutines.flow.MutableStateFlow

data class CityLabel(
    val cityTable: Table,
    val incomeLabel: Label,
    val labelGroup: HorizontalGroup,
    val populationGroup: HorizontalGroup,
    val population: MutableStateFlow<Int>,
    val targetPopulation: MutableStateFlow<Int>,
)
