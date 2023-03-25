package com.pixelatedmind.game.tinyuniverse.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream
import space.earlygrey.shapedrawer.ShapeDrawer

class WavetableDisplayPlanel : Actor() {
    init{
        this.color = Color(.15f,.15f,.15f,1f)
    }
    var waveform : FloatInputStream? = null
    var lineColor = Color(0f, .8f,.8f,1f)
    var axisColor = Color(.3f, .3f,.3f,1f)

    private val WAVEFORM_RENDER_STEPSIZE = 1
    private var shapeDrawer : ShapeDrawer? = null

    private fun drawXAxis(){
        shapeDrawer!!.setColor(axisColor)
        shapeDrawer!!.line(x, y+height/2, x+width,y+height/2)
    }
    override fun draw(batch: Batch?, parentAlpha: Float) {
        initShapeDrawer(batch!!)
        val waveformRef = waveform

        shapeDrawer!!.setColor(color)
        shapeDrawer!!.filledRectangle(x,y,width,height)
        drawXAxis()

        if(waveformRef!=null){
            val lastPoint = Vector2()

            shapeDrawer!!.setColor(lineColor)
            shapeDrawer!!.setDefaultLineWidth(2f)
            var i = 0
            while(i<width){
                val x1 = i / width
                val y1 = (waveformRef.read(x1) + 1) / 2f
                if(i!=0) {
                    shapeDrawer!!.line(
                            x + lastPoint.x * width, y + lastPoint.y * height,
                            x + x1 * width, y + y1 * height)
                }
                lastPoint.set(x1,y1)
                i+=WAVEFORM_RENDER_STEPSIZE
            }
        }
    }

    fun initShapeDrawer(batch : Batch){
        val region = if(shapeDrawer!=null){
            shapeDrawer!!.region
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

    override fun setBounds(x: Float, y: Float, width: Float, height: Float) {
        println("wavetable : $x $y $width $height")
        super.setBounds(x, y, width, height)
    }
}