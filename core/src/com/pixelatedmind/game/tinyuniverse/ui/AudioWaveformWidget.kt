package com.pixelatedmind.game.tinyuniverse.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.io.AudioStreamPlayer
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream
import space.earlygrey.shapedrawer.ShapeDrawer

class AudioWaveformWidget(streamPlayer : AudioStreamPlayer) : Actor() {

    private var audioBuffer : FloatArray? = null

    init{
        this.color = Color(.15f,.15f,.15f,1f)
        streamPlayer.addPlaybackListener {buffer->
            audioBuffer = buffer
        }
    }

    var lineColor = Color(0f, .8f,.8f,1f)
    var axisColor = Color(.3f, .3f,.3f,1f)

    private var shapeDrawer : ShapeDrawer? = null

    private fun drawXAxis(){
        shapeDrawer!!.setColor(axisColor)
        shapeDrawer!!.line(x, y+height/2, x+width,y+height/2)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        val buffer = audioBuffer
        if(buffer!=null){
            initShapeDrawer(batch!!)
            val sampleInc = if(width < buffer.size){
                (buffer.size / width).toInt()
            }else{
                1
            }
            val renderInc = if(width < buffer.size){
                1f
            }else{
                width / buffer.size
            }

            var sampleIndex = 0
            var renderX = 0f
            val lastPoint = Vector2()

            shapeDrawer!!.setColor(color)
            shapeDrawer!!.filledRectangle(x,y,width,height)
            drawXAxis()
            shapeDrawer!!.setColor(lineColor)
            shapeDrawer!!.setDefaultLineWidth(2f)
            val halfHeight = height / 2f
            while(renderX<width && sampleIndex < buffer.size){
                val value = buffer[sampleIndex]

                shapeDrawer!!.line(
                        x + lastPoint.x,
                        y + halfHeight + lastPoint.y * halfHeight,
                        x + renderX,
                        y + halfHeight + value * halfHeight)
                lastPoint.set(renderX, value)
                renderX += renderInc
                sampleIndex += sampleInc

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