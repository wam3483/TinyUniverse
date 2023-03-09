package com.pixelatedmind.game.tinyuniverse.maps.tiled

import com.badlogic.gdx.math.Rectangle
import com.pixelatedmind.game.tinyuniverse.datastructure.Bitmap
import com.pixelatedmind.game.tinyuniverse.extensions.rectangle.right
import com.pixelatedmind.game.tinyuniverse.extensions.rectangle.top
import com.pixelatedmind.game.tinyuniverse.generation.region.RegionModel

class RegionBitmap(val regionModel : RegionModel) : Bitmap {
    private val regionRects = mutableListOf<Rectangle>()
    private var bounds : Rectangle

    override fun getHeight():Int{
        return bounds.height.toInt()
    }

    override fun getWidth(): Int {
        return bounds.width.toInt()
    }

    init{
        regionRects.addAll(regionModel.mainRoomGraph.getAllValues().union(regionModel.hallways).union(regionModel.subrooms))
        bounds = calculateBounds()
    }

    private fun calculateBounds():Rectangle{
        var minX = Float.MAX_VALUE
        var minY = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE
        var maxY = Float.MIN_VALUE
        regionModel.mainRoomGraph.getAllValues().union(regionModel.hallways).union(regionModel.subrooms)
                .forEach{
                    minX = Math.min(minX, it.x)
                    maxX = Math.max(maxX, it.right())
                    minY = Math.min(minY, it.y)
                    maxY = Math.max(maxY, it.top())
                }
        return Rectangle(minX,minY,maxX-minX,maxY-minY)
    }

    override fun getValue(x: Int, y: Int): Boolean {
        val worldX = x+bounds.x
        val worldY = y+bounds.y
        if(regionRects.any{it.contains(worldX,worldY)}){
            return true
        }
        return false
    }
}