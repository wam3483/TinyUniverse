package com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope

import com.badlogic.gdx.math.Interpolation
import com.pixelatedmind.game.tinyuniverse.generation.math.InterpolatedPiecewiseFunction
import com.pixelatedmind.game.tinyuniverse.generation.math.PiecewiseTimeFunction
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

class EnvelopeImpl(ampFunctions : List<Pair<Float, PiecewiseTimeFunction>> = listOf(), var releaseFunction: Pair<Float, PiecewiseTimeFunction>) : AbstractEnvelope() {
    private val ampFunctions = mutableListOf<Pair<Float, PiecewiseTimeFunction>>()
    private var isReleased : Boolean
    private var lastAmplitudeRead = 0f
    private var iterationAmplitudeRead = 0f

    private var releasedFunctionTimestamp : Float? = null
    private var lastFunctionIncTimestamp : Float = 0f
    private var lastIndexRendered : Int = 0
    private var isComplete : Boolean = false

    init{
        this.ampFunctions.addAll(ampFunctions)
        isReleased = false
    }

    override fun release(){
        isReleased = true
        fireReleaseEvent()
    }

    fun addFunction(ampFunc : Pair<Float, PiecewiseTimeFunction>){
        ampFunctions.add(ampFunc)
        ampFunctions.sortBy{it.first}
    }

    fun removeFunction(ampFunc : Pair<Float, PiecewiseTimeFunction>){
        ampFunctions.remove(ampFunc)
        ampFunctions.sortBy{it.first}
    }

    fun clearFunctions(){
        ampFunctions.clear()
    }

    override fun isComplete() : Boolean{
        if(isReleased){
            return isComplete
        }
        return false
    }

    private fun findNextFuncIndex(timeInSecs : Float, index : Int) : Int{
        var i = index
        while(i<ampFunctions.size-1){
            val func1 = ampFunctions[i]
            val func2 = ampFunctions[i+1]
            if(timeInSecs >=func1.first && timeInSecs<=func2.first){
                return i
            }
            i++
        }
        return ampFunctions.size-1
    }
    override fun read(timeInSeconds: Float): Float {
        if(ampFunctions.size == 0){
            return 0f
        }
        if(isReleased){
            //first iteration since release event
            if(releasedFunctionTimestamp == null){
                releasedFunctionTimestamp = timeInSeconds
                lastAmplitudeRead = iterationAmplitudeRead
            }
            var secsReleaseState = timeInSeconds - releasedFunctionTimestamp!!
            isComplete = secsReleaseState >= releaseFunction.first
            val releaseAmp =  releaseFunction.second.read(lastAmplitudeRead, secsReleaseState)
            return releaseAmp
        }else {
            val ampFunc = ampFunctions[lastIndexRendered]
            if(timeInSeconds > ampFunc.first){
                val newLastIndexRendered = findNextFuncIndex(timeInSeconds, lastIndexRendered)
                if(newLastIndexRendered!=lastIndexRendered) {
                    lastFunctionIncTimestamp = timeInSeconds
                    lastAmplitudeRead = iterationAmplitudeRead
                    lastIndexRendered = newLastIndexRendered

//                    val startTime = ampFunctions[lastIndexRendered].first
//                    println("lastIndex="+lastIndexRendered+" startTime: "+startTime+" currentTime="+timeInSeconds+" lastfunctionTime="+
//                            timeInSeconds+" next function : "+lastIndexRendered)
//                    println("last amp read "+lastAmplitudeRead)
                }
            }
            val ampFunction = ampFunctions[lastIndexRendered].second
            iterationAmplitudeRead = ampFunction.read(lastAmplitudeRead,timeInSeconds-lastFunctionIncTimestamp)

            return iterationAmplitudeRead
        }
    }

    companion object {
        fun buildEnvelope(stream : FloatInputStream,
                          attackDurationSecs : Float,
                          releaseDurationSecs:Float,
                          maxAttackAmplitude: Float,
                          attackInterpolation : Interpolation = Interpolation.linear,
                          releaseInterpolation : Interpolation = Interpolation.linear) : Envelope {
            val list = mutableListOf(
                    Pair(attackDurationSecs, InterpolatedPiecewiseFunction(attackDurationSecs, maxAttackAmplitude, attackInterpolation))
            )
            val ampEnv = EnvelopeImpl(list, Pair(releaseDurationSecs, InterpolatedPiecewiseFunction(releaseDurationSecs, 0f, releaseInterpolation)))
            return AmpEnvelopeStream(ampEnv, stream)
        }

        fun buildEnvelope(floatInputStream : FloatInputStream) : Envelope {
            val list = mutableListOf(
                    Pair(.01f, InterpolatedPiecewiseFunction(.01f, 1f, Interpolation.linear))
            )
            val envelope = EnvelopeImpl(list,
                    Pair(.05f, InterpolatedPiecewiseFunction(.05f, 0f, Interpolation.linear)))
            return AmpEnvelopeStream(envelope, floatInputStream)
        }
    }
}