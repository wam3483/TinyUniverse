package com.pixelatedmind.game.tinyuniverse.generation.music.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream

class VolumeModulationWaveformDecorator(val waveform : FloatInputStream, var amplitude : Float) : FloatInputStream{
    override fun read(timeInSeconds: Float): Float {
        var value = waveform.read(timeInSeconds)
        value *= amplitude
        if(value < -1){
            value = -1f
        }else if(value > 1){
            value = 1f
        }
        return value
    }
}