//package com.pixelatedmind.game.tinyuniverse.maps.tiled
//
//import com.badlogic.gdx.maps.tiled.TiledMapTile
//import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
//import com.pixelatedmind.game.tinyuniverse.generation.world.model.Biome
//import com.pixelatedmind.game.tinyuniverse.generation.world.model.WorldModel
//
//class BiomeTileLayerMapper (val biomeTileMap : Map<Biome, TiledMapTile>){
//    fun mapTileLayers(worldModel: WorldModel) : List<TiledMapTileLayer> {
//        val layers = mapOf(
//                Biome.Tundra to TiledMapTileLayer(worldModel.map.size, worldModel.map[0].size,32,32),
//                Biome.Ocean to TiledMapTileLayer(worldModel.map.size, worldModel.map[0].size,32,32),
//                Biome.Forest to TiledMapTileLayer(worldModel.map.size, worldModel.map[0].size,32,32),
//                Biome.Mountains to TiledMapTileLayer(worldModel.map.size, worldModel.map[0].size,32,32),
//                Biome.Desert to TiledMapTileLayer(worldModel.map.size, worldModel.map[0].size,32,32),
//                Biome.Jungle to TiledMapTileLayer(worldModel.map.size, worldModel.map[0].size,32,32),
//                Biome.Grassland to TiledMapTileLayer(worldModel.map.size, worldModel.map[0].size,32,32)
//        )
//        var y = 0
//        while(y<worldModel.map[0].size){
//            var x = 0
//            while(x<worldModel.map.size){
//                val layer = layers[worldModel.map[x][y]]!!
//                val tile = biomeTileMap[worldModel.map[x][y]]!!
//                val cell = TiledMapTileLayer.Cell()
//                cell.tile = tile
//                layer.setCell(x,y,cell)
//                x++
//            }
//            y++
//        }
//
//        return layers.map{it.value}
//    }
//}