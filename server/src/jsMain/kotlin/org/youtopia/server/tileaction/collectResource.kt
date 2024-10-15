package org.youtopia.server.tileaction

import org.youtopia.data.CityUpgradeRewardRequest
import org.youtopia.data.GameEffect
import org.youtopia.data.Position
import org.youtopia.data.Resource
import org.youtopia.data.TileAction
import org.youtopia.data.diff.ActionResult
import org.youtopia.server.utils.buildGameEffectsList

internal fun collectResource(
    action: TileAction.CollectResource,
    pos: Int,
    addPopulation: MutableList<GameEffect>.(cell: Position) -> Unit,
    cityUpgradeRewardRequests: MutableList<CityUpgradeRewardRequest>,
) = ActionResult(buildGameEffectsList {
    when (action.resource) {
        Resource.Fish, Resource.Fruit, Resource.Game -> {
            add(GameEffect.UpdateStars(-2))
            add(GameEffect.RemoveResource(pos))
            addPopulation(pos)
        }
        else -> error("${action.resource} can't be collected!")
    }
}, cityUpgradeRewardRequests = cityUpgradeRewardRequests)
