package org.youtopia.server.generalaction

import org.youtopia.data.GeneralAction
import org.youtopia.data.diff.ActionResult
import org.youtopia.server.utils.buildGameEffectsList

fun performGeneralActionImpl(action: GeneralAction): ActionResult = ActionResult(buildGameEffectsList {
    when (action) {
        GeneralAction.PassTurn -> passTurn()
    }
})
