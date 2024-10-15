package org.youtopia.effects

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.youtopia.data.GameEffect
import org.youtopia.ui.UiStage

fun updateIncome(
    action: GameEffect.UpdateIncome,
    uiStage: UiStage,
    stars: MutableStateFlow<Int>,
    income: MutableStateFlow<Int>,
) {
    income.update { it + action.delta }
    uiStage.starsLabel.setText("${stars.value} (+${income.value})")
}
