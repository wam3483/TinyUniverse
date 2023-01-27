package com.pixelatedmind.game.tinyuniverse.graph

import com.badlogic.gdx.graphics.g3d.particles.values.MeshSpawnShapeValue
import com.badlogic.gdx.math.DelaunayTriangulator
import com.badlogic.gdx.math.Vector2
import java.util.*

class TriangleMeshGraph<T>(val vertices:List<GenericVector2<T>>){

    private var triangleMeshVertexIndexGraph : ShortArray
    init{
        val triangulator = DelaunayTriangulator()
        val triangleGraphIndices = triangulator.computeTriangles(pointsToFloatArray(), false)
        triangleMeshVertexIndexGraph = ShortArray(triangleGraphIndices.size)
        var index =0
        repeat(triangleGraphIndices.size){
            triangleMeshVertexIndexGraph[index] = triangleGraphIndices.get(index)
            index++
        }
    }

    fun getEdges():List<GenericVector2<T>>{
        var i = 0
        val edges = mutableListOf<GenericVector2<T>>()
        while(i<triangleMeshVertexIndexGraph.size){
            edges.add(vertices[triangleMeshVertexIndexGraph[i].toInt()])
            i++
        }
        return edges
    }

    fun getTrianglesByVertex(vertex:Vector2) : List<Triangle>{
        val index = vertices.indexOfFirst{it.x==vertex.x && it.y==vertex.y}
        val indicesOfVertexIndexInGraph = getIndicesForVertexIndex(index)

        val trianglesWithSharedVertex = indicesOfVertexIndexInGraph.map {
            var triangleStartOffset = it - it % 3
            val v1 = vertices[triangleMeshVertexIndexGraph[triangleStartOffset].toInt()]
            triangleStartOffset++
            val v2 = vertices[triangleMeshVertexIndexGraph[triangleStartOffset].toInt()]
            triangleStartOffset++
            val v3 = vertices[triangleMeshVertexIndexGraph[triangleStartOffset].toInt()]
            Triangle(v1,v2,v3)
        }
        return trianglesWithSharedVertex
    }

    private fun pointsToFloatArray() : FloatArray {
        val ary = FloatArray(vertices.size*2)
        vertices.forEachIndexed{ index, v->
            val i2 = index*2
            ary[i2] = v.x
            ary[i2+1] = v.y
        }
        return ary
    }

    private fun getChildrenByIndex(valueIndex:Int):List<Node<T>>?{
        if(valueIndex == -1){
            return null
        }
        val indicesOfVertexIndexInGraph = getIndicesForVertexIndex(valueIndex)
        val children = mutableListOf<Node<T>>()
        indicesOfVertexIndexInGraph.forEach{getChildrenFromIndex(it.toShort(), children)}
        return children
    }

    private fun getIndicesForVertexIndex(vertexIndex:Int):MutableList<Int>{
        val indicesOfVertexIndexInGraph = mutableListOf<Int>()
        triangleMeshVertexIndexGraph.forEachIndexed{ index, value->
            if(value==vertexIndex.toShort()){
                indicesOfVertexIndexInGraph.add(index)
            }
        }
        return indicesOfVertexIndexInGraph
    }

    //Given a specified index of a vertex in our triangle mesh, this function will return the other 2
    //vertices the vertex is connected to
    private fun getChildrenAsVerticesByIndex(index:Int, children:MutableList<GenericVector2<T>> = mutableListOf()){
        //this gives us the first index of this triangle
        var triangleStartOffset = index - index % 3
        repeat(3){
            if(triangleStartOffset!=index){
                children.add(vertices[triangleMeshVertexIndexGraph[triangleStartOffset].toInt()])
            }
            triangleStartOffset++
        }
    }

    fun getChildrenByVertex(vertex: Vector2) : List<GenericVector2<T>>?{
        val vertexIndex = vertices.indexOfFirst{it.x==vertex.x && it.y==vertex.y}
        val indicesOfVertexIndexInGraph = getIndicesForVertexIndex(vertexIndex)
        val children = mutableListOf<GenericVector2<T>>()
        indicesOfVertexIndexInGraph.forEach{getChildrenAsVerticesByIndex(it, children)}
        return children
    }

    //TODO adjust API to not return nodes anymore. Returning generic vectors instead, keep it simple.
    fun getChildren(nodeValue: T) : List<Node<T>>?{
        val vertexIndex = vertices.indexOfFirst{it.value == nodeValue}
        return getChildrenByIndex(vertexIndex)
    }

    private fun getChildrenFromIndex(index:Short, children:MutableList<Node<T>>){
        var triangleIndex = index - index % 3
        val nodeIndex = index.toInt()
        repeat(3){
            if(triangleIndex!=nodeIndex){
                children.add(Node(vertices[triangleMeshVertexIndexGraph[triangleIndex].toInt()].value))
            }
            triangleIndex++
        }
    }

    fun getSpanningTree(): Node<T> {
        val stack = Stack<T>()
        val visited = mutableSetOf<T>()
        stack.push(vertices[0].value)
        val graph = Node(vertices[0].value)
        while(stack.isNotEmpty()){
            val nodeValue = stack.pop()
            visited.add(nodeValue)
            val children = getChildren(nodeValue)
            val graphNode = graph.getNodeFor(nodeValue)
            if(children!=null && graphNode!=null){
                val unvisitedChildren = children.filter { !visited.contains(it.value) }
                unvisitedChildren.forEach {
                    visited.add(it.value)
                    //we found an edge in our spanning tree.
                    graphNode.children.add(Node(it.value))
                    stack.push(it.value)
                }
            }
        }
        return graph
    }
}