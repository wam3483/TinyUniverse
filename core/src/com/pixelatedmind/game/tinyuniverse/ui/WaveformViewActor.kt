package com.pixelatedmind.game.tinyuniverse.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import space.earlygrey.shapedrawer.ShapeDrawer

class WaveformViewActor : Actor() {
    var zoom : Float = 1f
    lateinit var shapeDrawer : ShapeDrawer

    fun initShapeDrawer(batch : Batch){
        val region = if(shapeDrawer!=null){
            shapeDrawer.region
        }else{
            val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
            val color = 0xFFFFFFFF
            pixmap.setColor(color.toInt())
            pixmap.drawPixel(0, 0)
            val texture = Texture(pixmap) //remember to dispose of later

            pixmap.dispose()
            TextureRegion(texture, 0, 0, 1, 1)
        }
        shapeDrawer = ShapeDrawer(batch, region)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        initShapeDrawer(batch!!)
        val iterationSamples = samples
        var index = 0f
        val indexInc = zoom
        var xValue = 0f
        shapeDrawer.setColor(Color.WHITE)
        shapeDrawer.filledRectangle(x,y,width,height)
        shapeDrawer.setColor(Color.BLACK)
        while(index < width && index<iterationSamples.size && xValue<width){
            val sampleY = iterationSamples[index.toInt()]
            shapeDrawer.filledCircle(index,sampleY,1f)
            index+= indexInc
            xValue++
        }
    }

    var samples : List<Float> = listOf()

    override fun act(delta: Float) {
        super.act(delta)
    }
}