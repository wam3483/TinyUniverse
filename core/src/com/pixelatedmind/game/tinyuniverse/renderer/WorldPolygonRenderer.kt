package com.pixelatedmind.game.tinyuniverse.renderer

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.EarClippingTriangulator
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.pixelatedmind.game.tinyuniverse.generation.world.WorldPolygonModel
import space.earlygrey.shapedrawer.ShapeDrawer

class WorldPolygonRenderer(worldPolygonModel : WorldPolygonModel){
    var color = Color.WHITE
    val borderPoints : List<Vector2>
    val indices : List<Int>
    init{
        val triangulator = EarClippingTriangulator()
        borderPoints = worldPolygonModel.flattenBorder()
        val pointFloatAry = toFloatArray(borderPoints, borderPoints.size)
        val shortIndices = triangulator.computeTriangles(pointFloatAry)
        var i = 0
        val temp = mutableListOf<Int>()
        while(i<shortIndices.size){
            temp.add(shortIndices[i].toInt())
            i++
        }
        indices = temp.toList()
    }

    fun render(renderer : ShapeDrawer){
        var i =0
        renderer.setColor(color)
        while(i<indices.size){
            val v1 = borderPoints[indices[i]]
            val v2 = borderPoints[indices[i+1]]
            val v3 = borderPoints[indices[i+2]]
            renderer.filledTriangle(v1,v2,v3)
            i += 3
        }
        i=0
//            while(i<indices.size){
//                val v1 = borderPoints[indices[i]]
//                val v2 = borderPoints[indices[i+1]]
//                val v3 = borderPoints[indices[i+2]]
//
//                renderer.setColor(Color.BLACK)
//                renderer.triangle(v1,v2,v3)
//                i += 3
//            }

//        i=0
//        renderer.setColor(0f,0f,0f,1f)
//        val vector2Ary = Array<Vector2>()
//        borderPoints.forEach{
//            vector2Ary.add(it)
//        }
//        renderer.path(vector2Ary)
    }



    private fun toFloatArray(vertices : List<Vector2>, size:Int) : FloatArray {
        val ary = FloatArray(size*2)
        var i = 0
        while(i<size){
            val v = vertices[i]
            val i2 = i*2
            ary[i2] = v.x
            ary[i2+1] = v.y
            i++
        }
        return ary
    }
}