package com.pixelatedmind.game.tinyuniverse.generation.region

import com.badlogic.gdx.math.Rectangle
import com.pixelatedmind.game.tinyuniverse.generation.RectangleFactory
import com.pixelatedmind.game.tinyuniverse.graph.Edge
import com.pixelatedmind.game.tinyuniverse.graph.GenericVector2
import com.pixelatedmind.game.tinyuniverse.graph.TriangleMeshGraph
import java.util.*


class RegionGenerator(private val rectangleFactory: RectangleFactory,
                      private val roomsToGenerate:Int,
                      private val random:Random) {

    fun getMainRooms(allRooms:List<Rectangle>) : List<Rectangle> {
        val avgWidth = allRooms.map{it.width}.sum()/allRooms.size * 1.25F
        val avgHeight = allRooms.map{it.height}.sum()/allRooms.size * 1.25F
        val rooms = allRooms.filter{
            it.width>=avgWidth || it.height >=avgHeight
        }
        return rooms
    }

    private fun roundDown(value:Float, units:Float) : Float {
        return value - (value % units)
    }

    fun newDelunuaryGraphFrom(roomRects : List<Rectangle>): TriangleMeshGraph<Rectangle> {
        val graphPoints = positionArrayFrom(roomRects)
        val delaunayGraph = TriangleMeshGraph(graphPoints)
        return delaunayGraph
    }

    fun newMainRoomGraph(): RegionModel {
        val allRooms = this.rectangleFactory.new(roomsToGenerate)
        val mainRooms = this.getMainRooms(allRooms)
        val connectedGraph = newDelunuaryGraphFrom(mainRooms)
        val spanningTree = connectedGraph.getSpanningTree()
        val edges = spanningTree.getEdges()
        val numEdgesToAdd = (edges.edges.size * .15f).toInt()
        var numAdded = 0
        var iterations = 0
        val maxIterations = 100
        while(numAdded<numEdgesToAdd){
            //just in case the graph winds up fully connected
            if(iterations>=maxIterations){
                break;
            }
            val edge = edges.edges[random.nextInt(edges.edges.size)]
            val n1children = connectedGraph.getChildren(edge.n1)!!
            val e2 = Edge(edge.n1, n1children[random.nextInt(n1children.size)].value)
            if (!edges.contains(e2)) {
                numAdded++
                iterations = 0
                edges.add(e2)
            }
        }

        val hallwaySolver = HallwaySolver(5f, edges, allRooms.filter{!mainRooms.contains(it)})
        val hallways = hallwaySolver.getHallways()

        return RegionModel(allRooms, hallwaySolver.getSubrooms(hallways), edges,  connectedGraph, hallways.hallways, hallways.doors)
    }

    fun positionArrayFrom(nonOverlappingRects : List<Rectangle>):List<GenericVector2<Rectangle>> {
        return nonOverlappingRects.map{GenericVector2(it, it.x,it.y)}
    }
}