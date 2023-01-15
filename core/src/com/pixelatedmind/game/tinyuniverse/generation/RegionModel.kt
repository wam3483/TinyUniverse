package com.pixelatedmind.game.tinyuniverse.generation

import com.badlogic.gdx.math.Rectangle

class RegionModel(val mainRooms:List<Rectangle>,
                  val subRooms:List<Rectangle>,
                  val hallways:List<Rectangle>,
                  val edges:List<Edge<Rectangle>>) {
}