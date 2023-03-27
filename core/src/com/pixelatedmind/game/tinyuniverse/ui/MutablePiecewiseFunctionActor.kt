package com.pixelatedmind.game.tinyuniverse.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import space.earlygrey.shapedrawer.ShapeDrawer
import kotlin.math.abs

class MutablePiecewiseFunctionActor(private val function: PiecewiseModel, var font : BitmapFont = BitmapFont()) : Widget() {

    init{
        this.color = Color(.15f,.15f,.15f,1f)
    }

    private var enabled : Boolean = true

    var pointMoveBoundaryColor = Color(1f, 1f, 0f, .75f)
    var interpolationPointColor = Color(0f, .8f,.8f,.5f)
    var selectedInterpolationPointBorderColor = Color(.8f, .8f, .8f, 1f)
    var selectedInterpolationPointColor = Color(0f, .2f,.2f,1f)

    var piecewiseFunctionColor = Color(0f, .8f,.8f,1f)
    var underFunctionCurveColor = Color(0f, .8f,.8f,.1f)

    var disabledPiecewiseFunctionColor = Color(0f, .4f,.8f,1f)
    var disabledUnderFunctionCurveColor = Color(0f, .4f,.8f,.1f)

    var majorGridLineColor = Color(.8f,.8f,.8f, .4f)
    var minorGridLineColor = Color(.4f,.4f,.4f, .2f)

    private val INTERPOLATION_POINT_FOCUS_DISTANCE = 10f
    private val CLICK_DISTANCE = 5f

    private var dragLimitXRight : Float = 0f
    private var dragLimitXLeft : Float = 0f

    private var selectedPiece : PiecewiseModel.Piece? = null

    private val pieceSelectionListeners = mutableListOf<(PiecewiseModel.Piece?)->Unit>()

    private var shapeDrawer : ShapeDrawer? = null

    fun addPieceSelectedListener(listener : (PiecewiseModel.Piece?)->Unit){
        pieceSelectionListeners.add(listener)
    }

    fun setEnabled(enabled : Boolean){
        this.enabled = enabled
        if(!enabled){
            this.selectedPiece = null
        }
    }

    fun localPointToLabelUnits(input : Vector2){
        val lblRange = abs(maxFunctionValue - minFunctionValue)
        input.x = (input.x / width) * lblRange
        input.y = (input.y / height)
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

    init {
        addListener(object : ClickListener() {
            private var touchDown = false
            private var touchDragged = false
            private var lastClickPosition = Vector2()

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                println("touchDown $x $y $pointer")
                if(enabled) {
                    selectPieceAtPoint(x, y, false)
                    touchDown = true
                }
                return true
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                println("touchUp $x $y $pointer")
                if(enabled) {
                    if (touchDown && !touchDragged) {
                        clickEvent(event, x, y)
                    }
                    touchDown = false
                    touchDragged = false
                }
            }

            override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                if(enabled){
                    if(selectedPiece!=null){
                        val normX = x / width
                        val x1 =
                                if(normX<dragLimitXLeft) dragLimitXLeft * width
                                else if(normX>dragLimitXRight) dragLimitXRight*width
                                else normX * width
                        val y1 =
                                if(y<0) 0f
                                else if(y>height) height
                                else y
                        selectedPiece!!.start.set(x1/width,y1/height)
                    }
                    touchDragged = true
                }
            }

            private fun internalPieceFocusedFireEvent(arg : PiecewiseModel.Piece?){
                if(arg!=null){
                    val pieces = function.getPieces()
                    val index = pieces.indexOf(arg)
                    dragLimitXLeft =
                        if(index == 0) 0f
                        else pieces[index-1].start.x
                    dragLimitXRight =
                        if(index == pieces.size-1) 1f
                        else pieces[index + 1].start.x
                    println("dragLimitXLeft=$dragLimitXLeft dragLimitXRight=$dragLimitXRight")
                }
                firePieceFocusedEvent(arg)
            }

            private fun selectPieceAtPoint(x : Float, y:Float, rightClick : Boolean){
                val temp = Vector2()
                val pieces = function.getPieces()
                var selectedPoint = pieces.firstOrNull {
                    temp.set(it.start)
                    temp.x *= width
                    temp.y *= height
                    val distance = temp.dst(x, y)
                    distance <= INTERPOLATION_POINT_FOCUS_DISTANCE
                }
                if(pieces.size > 2 && rightClick && selectedPoint!=null){
                    function.removePiece(selectedPoint!!)
                    selectedPoint = null
                }
                if (selectedPiece != selectedPoint) {
                    internalPieceFocusedFireEvent(selectedPoint)
                }
                selectedPiece = selectedPoint
            }

            private fun createInterpolationPointAt(x : Float, y : Float){
                val pieces = function.getPieces()
                val normX = x / width
                val normY = y / height
                var i = 1
                while(i<pieces.size){
                    val lastPiece = pieces[i-1]
                    val nextPiece = pieces[i]
                    if(normX >= lastPiece.start.x && normX <= nextPiece.start.x){
                        break;
                    }
                    i++
                }
                if(i<pieces.size){
                    val newPiece = PiecewiseModel.Piece(Vector2(normX,normY),"linear", Interpolation.linear)
                    function.add(i, newPiece)
                    internalPieceFocusedFireEvent(newPiece)
                }
            }

            private fun clickEvent(event: InputEvent?, x: Float, y: Float) {
                val clickPos = Vector2(x,y)
                //double click
                if(clickPos.dst(lastClickPosition)<=CLICK_DISTANCE){
                    lastClickPosition.set(x, y)
                    createInterpolationPointAt(x,y)
                }else {
                    val rightClick = event!!.button == 1
                    if(!rightClick){
                        lastClickPosition.set(x, y)
                    }
                    selectPieceAtPoint(x,y, rightClick)
                }
            }
        })
    }

    private fun firePieceFocusedEvent(arg : PiecewiseModel.Piece?){
        pieceSelectionListeners.forEach{
            it.invoke(arg)
        }
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        initShapeDrawer(batch!!)
        this.validate()

        shapeDrawer!!.setColor(color)
        shapeDrawer!!.filledRectangle(x,y,width,height)
        drawGrid(batch)
        drawPiecewiseFunction()
        drawPointMovingBoundaries()
        drawInterpolationPoints()
        if(selectedPiece!=null){
            selectedInterpolationPointBorderColor
        }
    }

    private fun getFunctionColor() : Color{
        if(enabled){
            return piecewiseFunctionColor
        }
        return disabledPiecewiseFunctionColor
    }

    private fun getUnderCurveColor() : Color{
        if(enabled){
            return underFunctionCurveColor
        }
        return disabledUnderFunctionCurveColor
    }

    private fun getInterpolationPointRenderRadius() : Float{
        if(enabled){
            return 5f
        }else{
            return 3f
        }
    }

    private var minFunctionValue : Float = 0f
    fun setMinFunctionValue(minValue : Float){
        minFunctionValue = minValue
    }
    private var maxFunctionValue : Float = 1f
    fun setMaxFunctionValue(maxValue : Float){
        maxFunctionValue = maxValue
    }

    private var majorGridLineCount = 4
    private var minorGridLineCount = 4
    fun setGridLines(majorGridLineCount : Int, minorGridLineCount: Int){
        this.majorGridLineCount = majorGridLineCount
        this.minorGridLineCount = minorGridLineCount
    }

    private var units : String = ""
    fun setUnitGridLabel(units : String){
        this.units = units
    }

    fun getUnitGridLabel() : String{
        return units
    }

    private fun drawGrid(batch: Batch?){
        val majorGridInterval =
                1f / majorGridLineCount
        val labelRange = abs(maxFunctionValue - minFunctionValue)
        val minorGridInterval = majorGridInterval / minorGridLineCount.toFloat()
        var majorIndex = 0
        while(majorIndex < majorGridLineCount+1){

            val runningGridSum = majorIndex * majorGridInterval
            val runningLabelGridSum = minFunctionValue + labelRange * runningGridSum
            val majorX = x+width * runningGridSum
            val majorY = y+height * runningGridSum

            font.draw(batch, "%.2f".format(runningLabelGridSum)+units, majorX, y)
            shapeDrawer!!.setColor(majorGridLineColor)
            shapeDrawer!!.line(majorX, y, majorX, y+height)
            shapeDrawer!!.line(x, majorY, x+width, majorY)

            shapeDrawer!!.setColor(minorGridLineColor)

            if(majorIndex<majorGridLineCount){
                var minorIndex = 0
                while(minorIndex < minorGridLineCount){
                    val minorX = majorX + width* minorIndex * minorGridInterval
                    val minorY = majorY + height* minorIndex * minorGridInterval
                    shapeDrawer!!.line(minorX, y, minorX, y+height)
                    shapeDrawer!!.line(x, minorY, x+width, minorY)
                    minorIndex++
                }
            }
            ++majorIndex
        }
    }
    private fun drawPiecewiseFunction(){
        var startX = 0f
        val resolution = 1f
        var lastTime = 0f
        var lastY = 0f

        shapeDrawer!!.setDefaultLineWidth(2f)
        val points = mutableListOf<Float>()
        shapeDrawer!!.setColor(getFunctionColor())
        while(startX<width){
            val currentTime = startX / width
            val output = function.evaluate(currentTime)

            val x1 = lastTime * width + x
            val x2 = currentTime * width + x
            val y1 = lastY * height + y
            val y2 = output * height + y
            shapeDrawer!!.line(x1,y1,x2,y2)

            lastY = output
            lastTime = currentTime
            startX += resolution
            points.add(x1)
            points.add(y1)
        }
        points.add(x+width)
        points.add(y)
        shapeDrawer!!.setColor(getUnderCurveColor())
        shapeDrawer!!.filledPolygon(points.toFloatArray())
    }

    private fun drawPointMovingBoundaries(){
        if(selectedPiece!=null) {
            shapeDrawer!!.setColor(pointMoveBoundaryColor)
            shapeDrawer!!.line(dragLimitXLeft*width+x, y, dragLimitXLeft*width+x, y+height)
            shapeDrawer!!.line(dragLimitXRight*width+x, y, dragLimitXRight*width+x, y+height)
        }
    }
    private fun drawInterpolationPoints(){
        val radius = getInterpolationPointRenderRadius()
        function.getPieces().forEach{
            val point = it.start
            if(selectedPiece != it) {
                shapeDrawer!!.setColor(interpolationPointColor)
                shapeDrawer!!.filledCircle(point.x * width + x, point.y * height + y, radius)
            }else{
                shapeDrawer!!.setColor(selectedInterpolationPointColor)
                shapeDrawer!!.filledCircle(point.x * width + x, point.y * height + y, radius)
                shapeDrawer!!.setColor(selectedInterpolationPointBorderColor)
                shapeDrawer!!.setDefaultLineWidth(2f)
                shapeDrawer!!.circle(point.x * width + x, point.y * height + y, radius)
            }
        }
    }

    companion object {
        private const val LINE_SEGMENT_LENGTH = 1f
    }
}