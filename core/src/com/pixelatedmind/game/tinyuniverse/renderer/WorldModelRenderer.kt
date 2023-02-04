package com.pixelatedmind.game.tinyuniverse.renderer

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.pixelatedmind.game.tinyuniverse.generation.world.model.WorldModel
import com.pixelatedmind.game.tinyuniverse.generation.world.model.WorldPolygonModel
import space.earlygrey.shapedrawer.JoinType
import space.earlygrey.shapedrawer.ShapeDrawer
import java.util.*

class WorldModelRenderer(val shapeDrawer: ShapeDrawer, val model: WorldModel, val renderConfig: WorldModelRendererConfig) {
    private val renderModels : List<WorldPolygonRenderer>
    private val distinctEdges : List<WorldPolygonModel.Edge>
    private val waterlinePaths : MutableList<Array<Vector2>>

    val bitmap = BitmapFont()
    init {
        renderModels = model.cellGraph.getVertices().map {
            val polygonModel = WorldPolygonRenderer(it)
            polygonModel.color = renderConfig.getColorFor(it.biome)
            polygonModel
        }
        distinctEdges = model.cellGraph.getVertices().map{it.borderEdges}.flatten().distinct()

        waterlinePaths = mutableListOf()
        initWaterlinePaths()
    }

    fun <T> Array<T>.addAll(other:List<T>){
        other.forEach{
            this.add(it)
        }
    }

    fun <T> LinkedList<T>.addAll(offset:Int, list:List<T>){
        var i = offset
        while(i<list.size){
            this.add(list.get(i))
            i++
        }
    }

    fun <T> LinkedList<T>.addAllFirst(offset:Int=0, list:List<T>){
        var i = offset
        while(i<list.size){
            this.addFirst(list[i])
            i++
        }
    }

    private fun getEdgeWithVector(v:Vector2?, edgeToIgnore: WorldPolygonModel.Edge?, edges:List<WorldPolygonModel.Edge>) : WorldPolygonModel.Edge?{
        if(v==null)
            return null
        return edges.firstOrNull{edgeToIgnore!=it && (it.borderPoints.first() == v || it.borderPoints.last()==v)}
    }

    private fun locateLastPath(edge: WorldPolygonModel.Edge, edges:MutableList<WorldPolygonModel.Edge>):Array<Vector2>{
        var first : Vector2? = edge.borderPoints.first()
        var last : Vector2? = edge.borderPoints.last()
        val path = LinkedList<Vector2>()
        path.addAll(edge.borderPoints)
        while(first!=null || last!=null) {
            val lastEdge = getEdgeWithVector(last, null, edges)
            val firstEdge = getEdgeWithVector(first, null, edges)

            if (lastEdge != null) {
                if (lastEdge.borderPoints.first() == last) {
                    //we know the first point is a duplicate, so we skip over it.
                    path.addAll(1, lastEdge.borderPoints)
                    first = lastEdge.borderPoints.last()
                }else{
                    //borderPoints.last must = first, so flip ary skip first
                    path.addAll(1, lastEdge.borderPoints.reversed())
                    first = lastEdge.borderPoints.first()
                }
                edges.remove(lastEdge)
            }else{
                first = null
            }

            if(firstEdge!=null){
                if (firstEdge.borderPoints.first() != first) {
                    //we know the last point is a duplicate
                    path.addAllFirst(1, firstEdge.borderPoints)
                    last = firstEdge.borderPoints.last()
                }else{
                    //we know the first point is a duplicate
                    path.addAllFirst(1, firstEdge.borderPoints.reversed())
                    last = firstEdge.borderPoints.first()
                }
                edges.remove(firstEdge)
            }else{
                last = null
            }
        }
        edges.remove(edge)
        val ary = Array<Vector2>()
        ary.addAll(path.distinct())
        if(path.first() == path.last()){
            if(ary.first() != path.first()){
                ary.insert(0, path.first())
            }else if(ary.last() != path.last()){
                ary.add(path.first())
            }
        }
        return ary
    }

    fun initWaterlinePaths(){
        val waterEdges = distinctEdges.filter{it.edgeType == WorldPolygonModel.EdgeType.Waterline}.toMutableList()

        while(waterEdges.isNotEmpty()){
            val currentEdge = waterEdges.first()
            val ary = locateLastPath(currentEdge, waterEdges)
            waterlinePaths.add(ary)
        }
    }


    fun render(){
        shapeDrawer.batch.begin()
        renderModels.forEach{
            it.render(shapeDrawer)
        }

        waterlinePaths.forEach{
            shapeDrawer.setColor(renderConfig.getColorFor(WorldPolygonModel.EdgeType.Waterline))

            shapeDrawer.path(it, renderConfig.waterlineEdgeThickness, JoinType.SMOOTH, true)
//            shapeDrawer.setColor(Color.GREEN)
//            shapeDrawer.filledCircle(it.first(), 1f)
//            shapeDrawer.setColor(Color.BLACK)
//            bitmap.draw(shapeDrawer.batch, it.first().toString()+" "+it.size.toString(), it.first().x, it.first().y)
//            shapeDrawer.setColor(Color.BLUE)
//            shapeDrawer.filledCircle(it.last(), 1f)
//            shapeDrawer.setColor(Color.BLACK)
//            bitmap.draw(shapeDrawer.batch, it.last().toString()+" "+it.size.toString(), it.last().x, it.last().y)
        }
        shapeDrawer.batch.end()
    }
}