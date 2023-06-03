package com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.stream.ReverbEffectStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.ui.model.ReverbModel

class ReverbEffectFactory(val model : ReverbModel): EffectFactory {
    override fun new(inputStream: FloatInputStream): FloatInputStream {
        val effect = ReverbEffect(model.delay.toDouble(), model.sampleRate, model.decay.toDouble())
        val stream = ReverbEffectStream(effect, inputStream)
        return stream
    }
}