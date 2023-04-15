package com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect

import com.pixelatedmind.game.tinyuniverse.generation.music.Notes
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

class UnisonEffect(val frequency : Float,
                   val streamFactory : (frequency : Float)-> FloatInputStream,
                   numVoices : Int,
                   detunePercent:Float) : FloatInputStream {

    private val notes = Notes()
    private val unisonStream : FloatInputStream = unisonEffect2(frequency, numVoices, detunePercent)

    private fun generateNormalizedIrrationals(count : Int) : List<Float> {
        var rootArg = 2.0
        var i = 0
        val result = mutableListOf<Float>()
        val perfectSquares = listOf(4.0, 9.0, 16.0, 25.0,36.0)
        while(i<count){
            if(perfectSquares.contains(rootArg)){
                rootArg++
            }
            val value = Math.sqrt(rootArg)
            result.add(value.toFloat())
            rootArg++
            i++
        }
        val normalMult = 1f / result.size
        return result.map{it * normalMult}
    }

    private fun unisonEffect2(frequency :Float, numStreams : Int, detuneCents : Float) : FloatInputStream{
        val noteBandwidth = notes.getNoteBandwidth(frequency)
        val detuneCentWidth = (numStreams - 1) * (detuneCents *100)
        val detuneFrequencyWidth = detuneCentWidth * (noteBandwidth / 100f)
        val detuneInc = detuneFrequencyWidth / numStreams


        var i = 0
        var detuneFreq = frequency - (detuneFrequencyWidth / 2)
        val streams = mutableListOf<FloatInputStream>()
        while(i < numStreams){
            val stream = streamFactory.invoke(detuneFreq)
            streams.add(stream)
            detuneFreq += detuneInc
            i++
        }
        val weights = mutableListOf<Float>()
        repeat(numStreams){
            weights.add(1f/numStreams)
        }
        return MultiplexGainEffect(streams, weights)
    }

    private fun unisonEffect(frequency : Float, numStreams : Int, detunePercent : Float): FloatInputStream {
        val perVoiceFrequencyMult = generateNormalizedIrrationals(numStreams+1)
        val detune = mutableListOf<Float>()
        var i =0
        var sum = 0f
        while(i<numStreams){
            val pitchRange = (perVoiceFrequencyMult[i+1] - perVoiceFrequencyMult[i])
            val delta = pitchRange * detunePercent
            val detuneValue = Math.pow(2.0, delta.toDouble()).toFloat()
            detune.add(detuneValue)
            sum += detuneValue
            i++
        }
        sum /= numStreams
        sum -= 1
        detune.forEachIndexed{ index, _ ->
            detune[index] -= sum
        }

        val streams = detune.map{
            streamFactory.invoke(frequency * it)
        }
        val weights = mutableListOf<Float>()
        repeat(numStreams){
            weights.add(1f/numStreams)
        }
        return MultiplexGainEffect(streams, weights)
    }

    override fun read(timeInSeconds: Float): Float {
        return unisonStream.read(timeInSeconds)
    }
}