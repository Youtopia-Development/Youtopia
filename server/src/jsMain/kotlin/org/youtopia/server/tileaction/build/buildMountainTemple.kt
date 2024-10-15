package org.youtopia.server.tileaction.build

import org.youtopia.data.Building
import org.youtopia.data.GameEffect
import org.youtopia.data.Position
import org.youtopia.data.Tile

fun MutableList<GameEffect>.buildMountainTemple(
    tile: Tile,
    pos: Position,
    addPopulation: MutableList<GameEffect>.(cell: Position) -> Unit,
) {
    add(GameEffect.UpdateStars(-20))
    if (tile.resource != null) {
        add(GameEffect.RemoveResource(pos))
    }
    add(GameEffect.Build(pos, Building.MountainTemple(1, 2)))
    addPopulation(pos)
}
