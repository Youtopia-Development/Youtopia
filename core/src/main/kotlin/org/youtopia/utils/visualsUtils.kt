package org.youtopia.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.youtopia.data.Building
import org.youtopia.data.CityUpgradeReward
import org.youtopia.data.Resource
import org.youtopia.data.Terrain
import org.youtopia.data.TileAction
import org.youtopia.data.Tribe

val tilesAtlas = TextureAtlas(Gdx.files.internal("visuals.atlas"))
val mapUiAtlas = TextureAtlas(Gdx.files.internal("map_ui_visuals.atlas"))

fun getResourceAtlasName(
    resource: Resource,
    tribe: Tribe,
): String = when (resource) {
    Resource.Game -> "game_${tribe.name.lowercase()}"
    Resource.Fruit -> "fruit_${tribe.name.lowercase()}"
    Resource.Metal -> "metal"
    Resource.Crop -> "crop"
    Resource.Fish -> "fish"
    Resource.Starfish -> "starfish"
}

fun getBuildingAtlasName(building: Building): String = when (building) {
    is Building.Bridge -> if (building.rotated) "bridge_reversed" else "bridge"
    is Building.City -> error("Cities should be constructed from houses via addCity()!")
    Building.Farm -> "farm"
    is Building.ForestTemple -> "forest_temple"
    is Building.Forge -> "forge"
    is Building.Lighthouse -> error("Lighthouses should be constructed from parts via addLighthouse()!")
    Building.LumberHut -> "lumber_hut"
    is Building.Market -> "market"
    Building.Mine -> "mine"
    is Building.Monument -> TODO()
    is Building.MountainTemple -> "mountain_temple"
    is Building.Port -> "port"
    Building.Ruin -> "ruin"
    is Building.Sawmill -> "sawmill"
    is Building.FieldTemple -> "temple"
    Building.Village -> "village"
    is Building.WaterTemple -> "water_temple"
    is Building.Windmill -> "windmill"
}

fun TileAction.image() = when (this) {
    is TileAction.Build -> getBuildingAtlasName(building)
    is TileAction.BuildRoad -> "build_road"
    is TileAction.BurnForest -> "burn_forest"
    is TileAction.ClearForest -> "clear_forest"
    is TileAction.CollectResource -> getResourceAtlasName(resource, tribe)
    is TileAction.DestroyBuilding -> "destroy"
    is TileAction.GrowForest -> "grow_forest"
    is TileAction.Recruit -> TODO()
}

fun getTerrainAtlasName(
    terrain: Terrain,
    tribe: Tribe,
    tileIndex: Int,
    mapSize: Pair<Int, Int>,
): String = when (terrain) {
    Terrain.OCEAN -> when {
        tileIndex == mapSize.first - 1 -> "ocean_left_right"
        tileIndex < mapSize.first - 1 -> "ocean_left"
        tileIndex % mapSize.first == mapSize.first - 1 -> "ocean_right"
        else -> "ocean"
    }
    Terrain.WATER -> when {
        tileIndex == mapSize.first - 1 -> "water_left_right"
        tileIndex < mapSize.first - 1 -> "water_left"
        tileIndex % mapSize.first == mapSize.first - 1 -> "water_right"
        else -> "water"
    }
    else -> "field_${tribe.name.lowercase()}"
}

fun CityUpgradeReward.image() = when (this) {
    CityUpgradeReward.WORKSHOP -> "reward_workshop"
    CityUpgradeReward.EXPLORER -> "reward_explorer"
    CityUpgradeReward.CITY_WALL -> "reward_city_wall"
    CityUpgradeReward.RESOURCES -> "reward_resources"
    CityUpgradeReward.POPULATION_GROWTH -> "reward_population_growth"
    CityUpgradeReward.BORDER_GROWTH -> "reward_border_growth"
    CityUpgradeReward.PARK -> "reward_park"
    CityUpgradeReward.SUPER_UNIT -> "reward_super_unit"
}
