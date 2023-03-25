package com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope

class EnvelopeFactoryImpl(private val factoryFunc : (Float)->Envelope) : EnvelopeFactory {
    override fun newEnvelope(frequency: Float): Envelope {
        return factoryFunc.invoke(frequency)
    }
}