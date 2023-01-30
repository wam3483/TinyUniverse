package com.pixelatedmind.game.tinyuniverse.graph

import com.badlogic.gdx.math.DelaunayTriangulator
import com.badlogic.gdx.math.Vector2

class DelaunayVoronoiGraphBuilder {
    fun toDelaunayGraph(pointCloud:List<Vector2>):Graph<Vector2>{
        val triangulator = DelaunayTriangulator()
        val triangleVertices = triangulator.computeTriangles(pointsToFloatArray(pointCloud), false)
        val graph = AdjacencyGraphImpl<Vector2>()
        val numTriangles = triangleVertices.size / 3
        var i = 0
        while(i<numTriangles){
            val i1 = triangleVertices[i]
            val i2 = triangleVertices[i+1]
            val i3 = triangleVertices[i+2]
            graph.addEdge(pointCloud[i1.toInt()], pointCloud[i2.toInt()])
            graph.addEdge(pointCloud[i2.toInt()], pointCloud[i3.toInt()])
            graph.addEdge(pointCloud[i3.toInt()], pointCloud[i1.toInt()])
            i += 3
        }
        return graph
    }

    private fun updateEdge(edgeMap:MutableMap<Edge<Vector2>, DelaunayVoronoiEdge>,
                   edge:Edge<Vector2>,
                   center:Vector2) : DelaunayVoronoiEdge{
        var dv = edgeMap[edge]
        if(dv !=null){
            dv!!.voronoiN2 = center
        }else{
            dv = DelaunayVoronoiEdge(edge.n1, edge.n2, center, null)
            edgeMap[edge] = dv
        }
        return dv
    }
    private fun updateVertex(map : MutableMap<Vector2, MutableSet<DelaunayVoronoiEdge>>,
                             vertex:Vector2, value:DelaunayVoronoiEdge){
        var cachedSet = map[vertex]
        if(cachedSet==null){
            cachedSet = mutableSetOf()
            map[vertex] = cachedSet
        }
        cachedSet.add(value)
    }

    fun buildDelaunayVoronoiGraph(pointCloud:List<Vector2>):DelaunayVoronoiGraph{
        val triangulator = DelaunayTriangulator()
        val triangleVertices = triangulator.computeTriangles(pointsToFloatArray(pointCloud), false)
        val tempTri = TriangleVectorImpl()
        val edgeMap = mutableMapOf<Edge<Vector2>, DelaunayVoronoiEdge>()
        val vertexMap = mutableMapOf<Vector2, MutableSet<DelaunayVoronoiEdge>>()

        var i = 0
        while(i<triangleVertices.size) {
            val i1 = triangleVertices[i].toInt()
            val i2 = triangleVertices[i + 1].toInt()
            val i3 = triangleVertices[i + 2].toInt()
            val v1 = pointCloud[i1]
            val v2 = pointCloud[i2]
            val v3 = pointCloud[i3]
            val center = tempTri.set(v1, v2, v3).centroid(Vector2())

            var e1 = Edge(v1, v2)
            var e2 = Edge(v2, v3)
            var e3 = Edge(v3, v1)

            val edge1To2 = updateEdge(edgeMap, e1, center)
            val edge2To3 = updateEdge(edgeMap, e2, center)
            val edge3To1 = updateEdge(edgeMap, e3, center)
            updateVertex(vertexMap, v1, edge1To2)
            updateVertex(vertexMap, v1, edge3To1)
            updateVertex(vertexMap, v2, edge1To2)
            updateVertex(vertexMap, v2, edge2To3)
            updateVertex(vertexMap, v3, edge2To3)
            updateVertex(vertexMap, v3, edge3To1)
            i+=3
        }

        val result= mutableMapOf<Vector2, List<DelaunayVoronoiEdge>>()
        vertexMap.keys.forEach{
            result[it] = vertexMap[it]!!.toList()
        }
        return DelaunayVoronoiGraph(result)
    }

    private fun pointsToFloatArray(vertices:List<Vector2>) : FloatArray {
        val ary = FloatArray(vertices.size*2)
        vertices.forEachIndexed{ index, v->
            val i2 = index*2
            ary[i2] = v.x
            ary[i2+1] = v.y
        }
        return ary
    }
}