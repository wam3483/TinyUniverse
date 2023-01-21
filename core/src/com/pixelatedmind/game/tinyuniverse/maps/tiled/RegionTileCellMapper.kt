package com.pixelatedmind.game.tinyuniverse.maps.tiled

import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer

class RegionTileCellMapper(val bitmap : Bitmap, val autoTile : List<TiledMapTile>) {
    private val autotileBitflagMap = mutableMapOf<Int,Int>()
    init{
        autotileBitflagMap.putAll(mapOf<Int,Int>(
                2 to 1,
                8 to 2,
                10 to 3,
                11 to 4,
                16 to 5,
                18 to 6,
                22 to 7,
                24 to 8,
                26 to 9,
                27 to 10,
                30 to 11,
                31 to 12,
                64 to 13,
                66 to 14,
                72 to 15,
                74 to 16,
                75 to 17,
                80 to 18,
                82 to 19,
                86 to 20,
                88 to 21,
                90 to 22,
                91 to 23,
                94 to 24,
                95 to 25,
                104 to 26,
                106 to 27,
                107 to 28,
                120 to 29,
                122 to 30,
                123 to 31,
                126 to 32,
                127 to 33,
                208 to 34,
                210 to 35,
                214 to 36,
                216 to 37,
                218 to 38,
                219 to 39,
                222 to 40,
                223 to 41,
                248 to 42,
                250 to 43,
                251 to 44,
                254 to 45,
                255 to 46,
                0 to 47 ))
    }

    private fun getAutotileBitflag(x:Int, y:Int) : Int{
        var bitflag = 0
        var bitIndex = 0
        for(dy in 1 downTo -1){
            val yIndex = y + dy
            for(dx in -1..1){
                val xIndex = x + dx
                //ignore center tile
                if(dy!=0 || dx!=0){
                    var bitValue = bitmap.getValue(xIndex,yIndex)
                    if(bitValue){
                        bitflag = bitflag or (1 shl bitIndex)
                    }
                    ++bitIndex
                }
            }
        }
        return bitflag
    }

    fun resolveCell(x:Int, y:Int) : TiledMapTileLayer.Cell{
        val bitflag = getAutotileBitflag(x,y)
        val tile = this.autoTile[bitflag]
        val cell = TiledMapTileLayer.Cell()
        cell.tile = tile
        return cell
    }
}