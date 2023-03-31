package com.pixelatedmind.game.tinyuniverse.ui.patch

import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.AbstractEnvelope
import com.pixelatedmind.game.tinyuniverse.ui.PiecewiseModel

class UIInteractableEnvelope(piecewiseModel : PiecewiseModel) : AbstractEnvelope() {
    private var released = false
    private var complete = false
    private val piecewiseStream : PiecewiseStream
    private val releaseStream : PiecewiseStream

    private var releasedTimestamp : Float? = null

    val releasePiecewiseModel : PiecewiseModel

    private val completeListeners = mutableListOf<()->Unit>()

    init{
        val pieces = piecewiseModel.getPieces()

        //second to last point is really the last function in the series as the last point
        //has no defined end point for interpolation.
        val releaseFunctionEnd = pieces[pieces.size-1]
        val releaseFunctionStart = pieces[pieces.size-2]


        val releaseStart = Vector2().set(releaseFunctionStart.start)
        releaseStart.x = 0f
        releasePiecewiseModel = PiecewiseModel(
                PiecewiseModel.Piece(releaseStart,"", releaseFunctionStart.interpolation),
                PiecewiseModel.Piece(Vector2().set(releaseFunctionEnd.start), "", releaseFunctionEnd.interpolation)
        )
        releasePiecewiseModel.startY = piecewiseModel.startY
        releasePiecewiseModel.endY = piecewiseModel.endY
        releasePiecewiseModel.timesToRepeat = 0
        releasePiecewiseModel.repeatForever = false
        val normalizedReleaseDuration = releaseFunctionEnd.start.x - releaseFunctionStart.start.x
        releasePiecewiseModel.durationSecs = normalizedReleaseDuration * piecewiseModel.durationSecs
        releasePiecewiseModel.name = piecewiseModel.name+" release"
        releaseStream = PiecewiseStream(releasePiecewiseModel)


        //create new model with all pieces except the last one in the sequence.
        //the last function in the sequece is the release function
        val piecesExcludingReleaseFunction = pieces.filterIndexed{index,value->index!=pieces.size-1}.map{
            PiecewiseModel.Piece(Vector2(it.start),it.interpolationName,it.interpolation)
        }
        val function = PiecewiseModel(piecesExcludingReleaseFunction)
        val rescaleMult = piecewiseModel.durationSecs / function.durationSecs

        piecesExcludingReleaseFunction.forEach{
            it.start.x *= rescaleMult
        }
        piecesExcludingReleaseFunction.last().start.x = 1f
        piecesExcludingReleaseFunction.first().start.x = 0f

        function.interactive = piecewiseModel.interactive
        function.timesToRepeat = piecewiseModel.timesToRepeat
        function.repeatForever = piecewiseModel.repeatForever
        //duration is reduced by the start point of the last peicewise function

        function.durationSecs = piecewiseModel.durationSecs - releasePiecewiseModel.durationSecs
        function.name = piecewiseModel.name
        function.startY = piecewiseModel.startY
        function.endY = piecewiseModel.endY
        piecewiseStream = PiecewiseStream(function)
    }

    override fun release() {
        released = true
        fireReleaseEvent()
    }

    override fun isComplete(): Boolean {
        if(released) {
            return complete
        }
        return false
    }

    private fun releasedRead(timeInSeconds: Float): Float {
        if(releasedTimestamp == null){
            releasedTimestamp = lastTimeInSeconds
            val startY = piecewiseStream.read(releasedTimestamp!!)
            releaseStream.model.getPieces()[0].start.y =startY
            println("release function : ["+releaseStream.model.getPieces()[0].start+" - "+releaseStream.model.getPieces()[1].start+"]")
            println("release function : duration: "+releaseStream.model.durationSecs)
        }
        val elapsedTimeSinceRelease = timeInSeconds - releasedTimestamp!!
        if(elapsedTimeSinceRelease >= releaseStream.model.durationSecs){
            complete = true
            completeListeners.forEach{
                it.invoke()
            }
            return 0f
        }
        val result = releaseStream.read(elapsedTimeSinceRelease)
//        println("release result: "+result+" elapsed="+elapsedTimeSinceRelease)
        return result
    }

    fun addCompleteListener(completeListener : ()->Unit){
        completeListeners.add(completeListener)
    }

    var lastTimeInSeconds = 0f
    override fun read(timeInSeconds: Float): Float {
        var result = 0f
        if(complete){
            result= 0f
        }
        else if(released){
            result = releasedRead(timeInSeconds)
        }else{
            result = piecewiseStream.read(timeInSeconds)
//            println("time=$timeInSeconds value=$result uiinteractable")
        }
        lastTimeInSeconds = timeInSeconds
        return result
    }
}