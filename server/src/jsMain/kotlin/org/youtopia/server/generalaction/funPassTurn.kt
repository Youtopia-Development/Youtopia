package org.youtopia.server.generalaction

import org.youtopia.server.api.Server.game
import org.youtopia.data.Building
import org.youtopia.data.GameEffect
import org.youtopia.server.utils.nextPlayer

fun MutableList<GameEffect>.passTurn() {
    add(GameEffect.PassTurn)
    // TODO: make sure to only send clients game actions they can see and render as current logic will break for >1 players
    add(GameEffect.UpdateStars(game.nextPlayer().income))
    game.tiles.forEachIndexed { index, tile ->
        if (tile.building is Building.Temple<*> && (tile.building as Building.Temple<*>).level < 5) {
            if ((tile.building as Building.Temple<*>).turnsToLevelUp == 1) {
                add(GameEffect.UpgradeBuilding(index, (tile.building as Building.Temple<*>).upgrade()))
            } else {
                add(GameEffect.DecrementTempleCounter(index))
            }
        }
    }
}
