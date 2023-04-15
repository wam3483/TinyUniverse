package com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect

import com.pixelatedmind.game.tinyuniverse.generation.music.filter.PassType
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.filter.HighLowPassFilterInputStream2
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.filter.LowPassFilter
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.ConstantStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.ui.PiecewiseModel
import com.pixelatedmind.game.tinyuniverse.ui.model.LowPassModel
import com.pixelatedmind.game.tinyuniverse.ui.patch.PiecewiseStream

class LowPassEffectFactory(val lowPassModel : LowPassModel) : EffectFactory {

    override fun new(inputStream: FloatInputStream): FloatInputStream {
        val cutoffStream = if(lowPassModel.cutoffFrequencyStream == null){
            ConstantStream(lowPassModel.cutoffFrequency)
        }else{
            val model = lowPassModel.cutoffFrequencyStream!!.copy()
            model.startY = lowPassModel.cutoffFrequencytStreamRange!!.min!!.toFloat()
            model.endY = lowPassModel.cutoffFrequencytStreamRange!!.max!!.toFloat()
            PiecewiseStream(model)
        }
        val resonanceStream = if(lowPassModel.resonanceStream == null){
            ConstantStream(lowPassModel.resonance)
        }else{
            PiecewiseStream(lowPassModel.resonanceStream!!)
        }

        val overdriveStream = if(lowPassModel.overdriveStream==null){
            ConstantStream(lowPassModel.overdrive)
        }else{
            PiecewiseStream(lowPassModel.overdriveStream!!)
        }
        val slope = lowPassModel.slope.slope
        return LowPassFilter(44100.0,cutoffStream,resonanceStream,overdriveStream,slope,inputStream)
    }
}