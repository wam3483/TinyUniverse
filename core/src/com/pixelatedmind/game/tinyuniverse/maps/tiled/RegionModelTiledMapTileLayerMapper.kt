package com.pixelatedmind.game.tinyuniverse.maps.tiled

import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.pixelatedmind.game.tinyuniverse.generation.region.RegionModel

class RegionModelTiledMapTileLayerMapper {
    fun map(regionModel : RegionModel, autoTile:List<TiledMapTile>) :TiledMapTileLayer{
        val bitmap = RegionBitmap(regionModel)
        val autoTileMapper = RegionTileCellMapper(bitmap,autoTile)
        var y = 0
        var tileSize = autoTile[0].textureRegion.regionWidth
        val columns = bitmap.getWidth()
        val rows = bitmap.getHeight()
        val tileLayer = TiledMapTileLayer(columns, rows, tileSize,tileSize)
        while(y<bitmap.getHeight()){
            var x = 0
            while(x<bitmap.getWidth()){
                var cell = autoTileMapper.resolveCell(x,y)
                tileLayer.setCell(x, y, cell)
                ++x
            }
            ++y
        }
        return tileLayer
    }
}