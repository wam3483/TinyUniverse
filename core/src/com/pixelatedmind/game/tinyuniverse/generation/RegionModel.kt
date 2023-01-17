package com.pixelatedmind.game.tinyuniverse.generation

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.graph.EdgeGraph
import com.pixelatedmind.game.tinyuniverse.graph.TriangleMeshGraph

class RegionModel(val mainRoomGraph: EdgeGraph<Rectangle>,
                  val subrooms : List<Rectangle>,
                  val fullyConnectedMainRoomPreGenGraph : TriangleMeshGraph<Rectangle>,
                  val hallways: List<Rectangle>,
                  val doors : List<Vector2>
) {
}