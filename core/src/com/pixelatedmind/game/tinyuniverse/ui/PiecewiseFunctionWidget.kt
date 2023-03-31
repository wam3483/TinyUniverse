package com.pixelatedmind.game.tinyuniverse.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Widget

class PiecewiseFunctionWidget(func : PiecewiseModel?) : Widget() {

    private val piecewiseActor : PiecewiseFunctionActor?

    init{
        piecewiseActor = PiecewiseFunctionActor(func)
//        piecewiseActor.debug = true
    }

    override fun getPrefWidth(): Float {
        return 100f
    }

    override fun getPrefHeight(): Float {
        return 100f
    }

    override fun setBounds(x: Float, y: Float, width: Float, height: Float) {
        super.setBounds(x, y, width, height)
        if(piecewiseActor!=null){
            println("piecewise function widget setBounds: $x $y $width $height")
//            piecewiseActor.setBounds(x,y,width,height)
        }
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        piecewiseActor?.width = width
        piecewiseActor?.height = height
        piecewiseActor?.y = y
        piecewiseActor?.x = x
        piecewiseActor?.draw(batch, parentAlpha)
    }
}