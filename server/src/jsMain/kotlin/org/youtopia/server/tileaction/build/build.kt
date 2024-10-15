package org.youtopia.server.tileaction.build

import org.youtopia.data.Building
import org.youtopia.data.CityUpgradeRewardRequest
import org.youtopia.data.GameEffect
import org.youtopia.data.PlayerId
import org.youtopia.data.Position
import org.youtopia.data.Tile
import org.youtopia.data.TileAction
import org.youtopia.data.diff.ActionResult
import org.youtopia.server.api.Server.roadNetworks
import org.youtopia.server.utils.buildGameEffectsList

internal fun build(
    action: TileAction.Build,
    pos: Position,
    tile: Tile,
    tileOwner: PlayerId?,
    addPopulation: MutableList<GameEffect>.(cell: Position) -> Unit,
    cityUpgradeRewardRequests: MutableList<CityUpgradeRewardRequest>,
): ActionResult {
    return ActionResult(buildGameEffectsList {
        when (action.building) {
            Building.LumberHut -> buildLumberHut(tile, pos, tileOwner, addPopulation)
            Building.Farm -> buildFarm(pos, tileOwner, addPopulation)
            Building.Mine -> buildMine(pos, tileOwner, addPopulation)
            is Building.Sawmill -> buildSawmill(tile, pos, tileOwner, addPopulation)
            is Building.Windmill -> buildWindmill(tile, pos, tileOwner, addPopulation)
            is Building.Forge -> buildForge(tile, pos, tileOwner, addPopulation)
            is Building.Market -> buildMarket(tile, pos, tileOwner)
            is Building.Port -> buildPort(tile, pos, addPopulation)
            is Building.Bridge -> buildBridge(tile, pos, (action.building as Building.Bridge).rotated, addPopulation)
            is Building.Monument -> TODO()
            is Building.FieldTemple -> buildFieldTemple(tile, pos, addPopulation)
            is Building.MountainTemple -> buildMountainTemple(tile, pos, addPopulation)
            is Building.ForestTemple -> buildForestTemple(tile, pos, addPopulation)
            is Building.WaterTemple -> buildWaterTemple(tile, pos, addPopulation)
            else -> error("${action.building} cannot be built!")
        }
    }, roadNetworks, cityUpgradeRewardRequests)
}
