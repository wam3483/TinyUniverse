package com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

class GainEffect(var waveform : FloatInputStream, decibelOffset : Float) : FloatInputStream {
    private var volumeNormOffset = 0f
    private var volumeDecibelOffset = 0f
    init{
       setVolume(decibelOffset)
    }
    fun setVolume(decibelOffset : Float){
        volumeDecibelOffset = decibelOffset
        volumeNormOffset = decibelOffset//MusicUtils.normDb(volumeDecibelOffset)
    }
    override fun read(timeInSeconds: Float): Float {
        var value = waveform.read(timeInSeconds)
        value *= (volumeNormOffset+1)
        return value
    }
}