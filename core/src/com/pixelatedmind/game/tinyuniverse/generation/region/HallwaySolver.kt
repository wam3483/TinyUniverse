package com.pixelatedmind.game.tinyuniverse.generation.region

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.extensions.rectangle.*
import com.pixelatedmind.game.tinyuniverse.graph.Edge
import com.pixelatedmind.game.tinyuniverse.graph.EdgeGraph

class HallwaySolver(val hallwaySize:Float, val mainRooms: EdgeGraph<Rectangle>, val potentialSubrooms:List<Rectangle>) {

    private fun getVerticalHallway(edge: Edge<Rectangle>, xAxisOverlap:Range) : Rectangle{
        val left = xAxisOverlap.mid()-hallwaySize/2
        val rect = Rectangle()
        rect.x = left
        rect.width = hallwaySize
        if(edge.n1.value.y<edge.n2.value.y){
            rect.y = edge.n1.value.top()
            rect.height = edge.n2.value.y-edge.n1.value.top()
        }
        else{
            rect.y = edge.n2.value.top()
            rect.height = edge.n1.value.y - edge.n2.value.top()
        }
        return rect
    }

    private fun getHorizontalHallway(edge: Edge<Rectangle>, yAxisOverlap:Range) : Rectangle{
        val rect = Rectangle()
        rect.y = yAxisOverlap.mid()-hallwaySize/2
        rect.height = hallwaySize
        if(edge.n1.value.right()<edge.n2.value.x){
            rect.x = edge.n1.value.right()
            rect.width = edge.n2.value.x - edge.n1.value.right()
        }else{
            rect.x = edge.n2.value.right()
            rect.width = edge.n1.value.x - edge.n2.value.right()
        }
        return rect
    }

    fun getSubrooms(hallways: HallwaysAndDoors):List<Rectangle>{
        return potentialSubrooms.filter{subroom->
            hallways.hallways.any{h->h.overlaps(subroom)}
        }
    }

    fun getHallways() : HallwaysAndDoors {
        val mainRoomRects = mainRooms.getAllValues()
        val hallways = mutableListOf<Rectangle>()
        val doors = mutableListOf<Vector2>()
        mainRooms.edges.forEach {
            val sharedRoomWall = it.n1.value.getSharedEdge(it.n2.value)
            if(sharedRoomWall!=null){
                doors.add(sharedRoomWall.midPoint())
            }else{
                val xOverlap = it.n1.value.xAxisOverlap(it.n2.value)
                val yOverlap = it.n1.value.yAxisOverlap(it.n2.value)
                //both should never be non-null at the same time
                //
                //if both are null OR
                //overlapped on X axis but overlap was too small OR
                //overlapped on y axis but overlap was too small
                //  then we do L shape hallway
                if((xOverlap==null && yOverlap==null) ||
                        (xOverlap!=null && xOverlap.size()<hallwaySize) ||
                        (yOverlap!=null && yOverlap.size()<hallwaySize)){

                    var topRect = it.n1.value
                    var bottomRect = it.n2.value
                    if(topRect.y<bottomRect.y){
                        topRect = it.n2.value
                        bottomRect = it.n1.value
                    }

                    var leftRect = it.n1.value
                    var rightRect = it.n2.value
                    if(leftRect.x>rightRect.x){
                        leftRect = it.n2.value
                        rightRect = it.n1.value
                    }

                    val vertRect = Rectangle()
                    val horizRect = Rectangle()

                    vertRect.y = bottomRect.top()
                    vertRect.height = topRect.midY() + hallwaySize / 2 - bottomRect.top()
                    vertRect.x = bottomRect.midX() - hallwaySize / 2
                    vertRect.width = hallwaySize

                    horizRect.y = topRect.midY() - hallwaySize / 2
                    horizRect.height = hallwaySize
                    //first try connecting hallway from the top of the lower rectangle. this should handle up & left|right (T) connections
                    //later we'll handle "upside down" T connections by extending rect from bottom of higher rect.
                    var topLeft = topRect == leftRect
                    if(topLeft) {
                        horizRect.x = leftRect.right()
                        horizRect.width = rightRect.midX() + hallwaySize / 2 - leftRect.right()
                        horizRect.width = rightRect.midX() + hallwaySize / 2 - leftRect.right()
                    }else{
                        horizRect.x = vertRect.x
                        horizRect.width = rightRect.x-vertRect.x
                    }
                    if(mainRoomRects.none{it.overlaps(horizRect)||it.overlaps(vertRect)}){
                        if(topLeft){
                            doors.add(Vector2(leftRect.right(), horizRect.midY()))
                        }else{
                            doors.add(Vector2(horizRect.right(), horizRect.midY()))
                        }
                        doors.add(Vector2(vertRect.midX(),vertRect.y))
                        hallways.add(horizRect)
                        hallways.add(vertRect)
                    }else{
                        vertRect.x = topRect.midX()-hallwaySize/2
                        vertRect.width = hallwaySize
                        vertRect.y = bottomRect.midY()-hallwaySize/2
                        vertRect.height = topRect.y-vertRect.y
                        hallways.add(vertRect)
                        doors.add(Vector2(vertRect.midX(),vertRect.top()))

                        horizRect.y = bottomRect.midY()-hallwaySize/2
                        horizRect.height = hallwaySize
                        if(bottomRect == rightRect){
                            horizRect.x = vertRect.x
                            horizRect.width = bottomRect.x-vertRect.x
                            doors.add(Vector2(horizRect.right(),horizRect.midY()))
                        }else{
                            horizRect.x = bottomRect.right()
                            horizRect.width = vertRect.x - horizRect.x
                            doors.add(Vector2(horizRect.x, horizRect.midY()))
                        }
                        hallways.add(horizRect)
                    }
                }
                //single vertical hallway
                else if(xOverlap!=null && xOverlap.size()>=hallwaySize){
                    val verticalHallway = getVerticalHallway(it, xOverlap)
                    doors.add(Vector2(verticalHallway.midX(),verticalHallway.y))
                    doors.add(Vector2(verticalHallway.midX(),verticalHallway.top()))
                    hallways.add(verticalHallway)
                }
                //single horizontal hallway
                else if(yOverlap!=null && yOverlap.size()>=hallwaySize){
                    val horizontalHallway = getHorizontalHallway(it, yOverlap)
                    doors.add(Vector2(horizontalHallway.x, horizontalHallway.midY()))
                    doors.add(Vector2(horizontalHallway.right(), horizontalHallway.midY()))
                    hallways.add(horizontalHallway)
                }
            }
        }
        return HallwaysAndDoors(doors, hallways)
    }
}