package com.pixelatedmind.game.tinyuniverse.graph

class VoronoiGraph<T>(points: List<GenericVector2<T>>) {
    val delaunayGraph = TriangleMeshGraph(points)
    private val voronoiGraphSites = mutableListOf<Triangle>()//<-- each deluanay triangle
    private val voronoiGraphAdjacencyList = mutableListOf<Int>() //<-- edge defined by pairs of voronoiGraphSite indices
    private val cells = mutableListOf<VoronoiCell>()
    init{
        initVoronoiFromDelaunay()
    }

    fun getVoronoiCells():List<VoronoiCell>{
        return cells
    }

    private fun isKnownEdge(i1:Int, i2:Int) : Boolean{
        var index = 0;
        while(index<voronoiGraphAdjacencyList.size){
            val first = voronoiGraphAdjacencyList[index]
            val second =voronoiGraphAdjacencyList[index+1]
            if((first==i1 && second==i2) || (first==i2 && second == i1)){
                return true
            }
            index+=2
        }
        return false
    }

    fun getEdges():List<Triangle>{
        return voronoiGraphAdjacencyList.map{voronoiGraphSites[it]}
    }

    private fun indexOfVoronoiEdge(t1:Triangle, t2:Triangle):Int{
        var i = 0
        var i1 = voronoiGraphSites.indexOf(t1)
        var i2 = voronoiGraphSites.indexOf(t2)
        while(i<voronoiGraphAdjacencyList.size){
            val s1 = voronoiGraphAdjacencyList[i]
            val s2 = voronoiGraphAdjacencyList[i+1]
            if((s1 == i1 && s2==i2)){// || (s1==i2 && s2==i1)){
                return i
            }
            i+=2
        }
        return -1
    }

    private fun initVoronoiFromDelaunay(){
        val voronoiGraphSitesMap = mutableSetOf<Triangle>()
        delaunayGraph.vertices.forEach { vertex ->
            //get set of triangles set1 which all share given vertex
            val triangles = delaunayGraph.getTrianglesByVertex(vertex)
            voronoiGraphSitesMap.addAll(triangles)
        }
        this.voronoiGraphSites.addAll(voronoiGraphSitesMap)

        val trianglesWithSharedEdge = mutableListOf<Triangle>()
        delaunayGraph.vertices.forEach{vertex->

            val triangles = delaunayGraph.getTrianglesByVertex(vertex)

            val voronoiCellIndices = mutableListOf<Int>()
            //set of edges produced by traversing triangles with a shared vertex should be a voronoi cell/region
            triangles.forEach{triangle->
                    //find triangles with shared edges
                    trianglesWithSharedEdge.clear()
                    var i = 0
                    while(i<triangles.size){
                        if (triangles[i].hasSharedEdge(triangle)) {
                            trianglesWithSharedEdge.add(triangles[i])
                        }
                        if (trianglesWithSharedEdge.size >= 3) {
                            break
                        }
                        ++i
                    }

                    trianglesWithSharedEdge.forEach{
                        var edgeIndex = indexOfVoronoiEdge(triangle, it)
                        if(edgeIndex == -1) {
                            val i1 = voronoiGraphSites.indexOf(triangle)
                            val i2 = voronoiGraphSites.indexOf(it)
                            voronoiGraphAdjacencyList.add(i1)
                            voronoiGraphAdjacencyList.add(i2)
                            voronoiCellIndices.add(i1)
                            voronoiCellIndices.add(i2)
                        } else {
                            if(edgeIndex % 2 == 1){
                                edgeIndex--
                            }
                            voronoiCellIndices.add(voronoiGraphAdjacencyList[edgeIndex])
                            voronoiCellIndices.add(voronoiGraphAdjacencyList[edgeIndex + 1])
                        }
                    }
                }
                cells.add(VoronoiCell(voronoiCellIndices, voronoiGraphSites))
            }
        }

    }
