package org.youtopia.server.tileaction

import org.youtopia.server.api.Server.game
import org.youtopia.data.GameEffect
import org.youtopia.data.Resource
import org.youtopia.data.Terrain
import org.youtopia.data.Tile
import org.youtopia.data.diff.ActionResult
import org.youtopia.server.utils.buildGameEffectsList

internal fun burnForest(
    pos: Int,
    tile: Tile,
) = ActionResult(buildGameEffectsList {
    add(GameEffect.UpdateStars(-5))
    if (tile.resource != null) {
        add(GameEffect.RemoveResource(pos))
    }
    add(GameEffect.SwapTerrain(pos, Terrain.FIELD, game.tiles[pos].tribe))
    add(GameEffect.AddResource(pos, Resource.Crop, game.tiles[pos].tribe))
})
