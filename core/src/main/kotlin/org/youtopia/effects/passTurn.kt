package org.youtopia.effects

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.youtopia.ui.UiStage

fun passTurn(
    uiStage: UiStage,
    turn: MutableStateFlow<Int>,
) {
    turn.update { it + 1 }
    uiStage.turnLabel.setText("${turn.value}")
}
