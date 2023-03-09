package com.pixelatedmind.game.tinyuniverse.datastructure

///If cyclic boundaries are set, out of bounds index references will be modulated across respective dimensions
class BitmapImpl(private val grid : List<List<Boolean>>, private val cyclicBoundaries : Boolean = false) : Bitmap{

    private var rotate = false

    fun rotate90(rotate:Boolean){
        this.rotate = rotate
    }

    override fun getWidth(): Int {
        if(rotate){
            return grid[0].size
        }
        return grid.size
    }

    override fun getHeight(): Int {
        if(rotate){
            return grid.size
        }
        return grid[0].size
    }

    override fun getValue(x: Int, y: Int): Boolean {
        val width = getWidth()
        val height = getHeight()
        var x1 = x
        var y1 = y
        if(cyclicBoundaries){
            if(Math.abs(x1) > width){
                x1 %= width
            }
            if(x1<0){
                x1 += width
            }
            if(Math.abs(y1) > height){
                y1 %= height
            }
            if(y1<0){
                y1 += height
            }
        }
        if(rotate){
            return grid[y1][x1]
        }else {
            return grid[x1][y1]
        }
    }
}