package com.pixelatedmind.game.tinyuniverse.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import space.earlygrey.shapedrawer.ShapeDrawer

class PiecewiseFunctionActor(private var piecewiseModel : PiecewiseModel? = null) : Actor() {

    var piecewiseFunctionColor = Color(0f, .8f,.8f,1f)
    var underFunctionCurveColor = Color(0f, .8f,.8f,.1f)
    private val functionPoints = mutableListOf<Float>()

    private var shapeDrawer : ShapeDrawer? = null
    init{
        setPiecewise(piecewiseModel)
    }
    fun setPiecewise(model : PiecewiseModel?){
        this.piecewiseModel = model
        invalidateModel()
    }

    override fun setY(y: Float) {
        super.setY(y)
        println("peicewise actor y=$y")
    }

    override fun setY(y: Float, alignment: Int) {
        super.setY(y, alignment)
        println("peicewise actor y=$y alignment=$alignment")
    }

    override fun setPosition(x: Float, y: Float) {
        super.setPosition(x, y)
        println("peicewise actor setPosition x=$x y=$y")
    }

    override fun setPosition(x: Float, y: Float, alignment: Int) {
        super.setPosition(x, y, alignment)
        println("peicewise actor x=$x y=$y alignment=$alignment")
    }

    override fun setBounds(x: Float, y: Float, width: Float, height: Float) {
        println("piecewise actor setBounds: x=$x y=$y w=$width h=$height")
        super.setBounds(x, y, width, height)
    }


    fun invalidateModel(){
        var startX = 0f
        val resolution = .01f
        synchronized(functionPoints){
            functionPoints.clear()
            if(piecewiseModel!=null){
                while(startX<1){
                    val output = piecewiseModel!!.evaluate(startX)
                    functionPoints.add(startX)
                    functionPoints.add(output)
                    startX += resolution
                }
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

    private fun drawLine(x1:Float, y1:Float, x2:Float, y2:Float){
        shapeDrawer!!.line(
                x1*width+x,
                y1*height+y,
                x2*width+x,
                y2*height+y)
    }

    private fun drawFunction(){
        shapeDrawer!!.setColor(piecewiseFunctionColor)
        val funct = piecewiseModel
        val resolution = .01f
        if(this.debug)
            println("draw piecewise actor $x $y $width $height")
        if(funct!=null){
            var startX = 0f
            var lastX :Float? = null
            var lastY :Float? = null
            while(startX<1){
                val output = funct.evaluate(startX)
                if(lastX!=null){
                    val x1 = lastX
                    val y1 = lastY!!

                    val x2 = startX
                    val y2 = output
                    if(debug) {
//                        println("draw piecewise actor func: $x1 $y1 $x2 $y2")
                    }
                    drawLine(x1,y1,x2,y2)
                }
                lastX = startX
                lastY = output
                startX += resolution
            }
        }
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        initShapeDrawer(batch!!)
        drawFunction()
    }
}