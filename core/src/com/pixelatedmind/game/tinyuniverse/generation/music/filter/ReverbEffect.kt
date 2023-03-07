package com.pixelatedmind.game.tinyuniverse.generation.music.filter

import com.pixelatedmind.game.tinyuniverse.datastructure.CircularArray
import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream

class ReverbEffect(val stream : FloatInputStream, val sampleRate : Int, val dryWetRatio : Float, delaySecs : Float, decayFactor : Float) : FloatInputStream {

    private val combFilter1 : ReverbCombFilter
    private val combFilter2 : ReverbCombFilter
    private val combFilter3 : ReverbCombFilter
    private val combFilter4 : ReverbCombFilter

    private val allPassFilter1 : ReverbAllPassFilter
    private val allPassFilter2 : ReverbAllPassFilter
    init{
        combFilter1 = ReverbCombFilter(delaySecs, decayFactor)
        combFilter2 = ReverbCombFilter(delaySecs-.01173f, decayFactor-.1313f)
        combFilter3 = ReverbCombFilter(delaySecs+.01931f, decayFactor-.2743f)
        combFilter4 = ReverbCombFilter(delaySecs-.00797f, decayFactor-.31f)

        allPassFilter1 = ReverbAllPassFilter("AllPass1",sampleRate)
        allPassFilter2 = ReverbAllPassFilter("AllPass2",sampleRate)
    }

    override fun read(timeInSeconds: Float): Float {
        val sample = stream.read(timeInSeconds)
        val comb1 = combFilter1.filter(timeInSeconds, sample)
        val comb2 = combFilter2.filter(timeInSeconds, sample)
        val comb3 = combFilter3.filter(timeInSeconds, sample)
        val comb4 = combFilter4.filter(timeInSeconds, sample)

        val combOutput = (comb1 + comb2 + comb3 + comb4)
        val dryWetOutput = (1 - dryWetRatio) * sample + dryWetRatio * combOutput

        val allPass1Output = allPassFilter1.filter(timeInSeconds, dryWetOutput) / 6f
        val allPass2Output = allPassFilter2.filter(timeInSeconds, allPass1Output)/3f

//        println("combOutput = $combOutput dryWet=$dryWetOutput allPass1=$allPass1Output allPass2=$allPass2Output")

        //not sure if dividing by 6 will work. source normalized audio by maximum over some period of time. i dislike this as it can cause
        //weird volume changes over time I think...
        return allPass2Output
    }
}

class ReverbCombFilter(val delaySecs : Float, val decayFactor : Float){
    fun filter(elapsedSecs : Float, sample : Float) : Float{
        if(elapsedSecs<delaySecs){
            return sample
        }else{
            return sample * decayFactor
        }
    }
}

class ReverbAllPassFilter(val name : String, val sampleRate : Int, val delaySecs : Float = .08927f, val decayFactor : Float = .131f){
    val ary = CircularArray<Pair<Float, Float>>(sampleRate)
    val lookAheadSecs = 20f / sampleRate

    private fun getSampleByTime(timestamp : Float, currentElapsedSecs : Float) : Float{
        val deltaSecs = currentElapsedSecs - timestamp
        val deltaSamples = (deltaSecs / sampleRate).toInt()
        //TODO it's probably smarter to do a binary search for the element closest in time to timestamp
        //and interpolate if the precise value is missing.
        val sampleIndex = ary.size - 1 - deltaSamples
        return ary[sampleIndex].second
    }

    val debug = false
    private fun debug(line : String){
        if(debug)
            println(line)
    }

    fun filter(elapsedSecs : Float, sample : Float) : Float{
        //TODO should i try to reuse pairs to save memory? would need a mutable pair object, default Pair impl not mutable.
        ary.add(Pair(elapsedSecs, sample))
        var result = sample
        val earlierSampleTimestamp = elapsedSecs - delaySecs
        val earlierSample = getSampleByTime(earlierSampleTimestamp,elapsedSecs)
        debug("$name: sample at $earlierSampleTimestamp secs = $earlierSample")
        if(elapsedSecs>=delaySecs){
            debug("$name: Mult1 = $result += -$decayFactor * $earlierSample")
            result += -decayFactor * earlierSample//get sample from elapsedSecs - delaySecs
        }
        if(elapsedSecs > delaySecs){
            val earlierSample2 = getSampleByTime(earlierSample + lookAheadSecs, elapsedSecs)
            debug("$name: Mult2 = $result += $decayFactor * $earlierSample2")
            result += decayFactor * earlierSample2
        }
        return result
    }
}