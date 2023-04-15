package com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope

class MultiplexEnvelopeFactory : EnvelopeFactory {
    private val factories = mutableListOf<EnvelopeFactory>()

    fun clear(){
        factories.clear()
    }

    fun addFactory(envelopeFactory : EnvelopeFactory){
        synchronized(factories) {
            factories.add(envelopeFactory)
        }
    }

    fun removeFactory(envelopeFactory: EnvelopeFactory){
        synchronized(factories){
            factories.remove(envelopeFactory)
        }
    }

    private fun getEnvelopes(frequency : Float) : List<Envelope> {
        synchronized(factories) {
            return factories.map {
                it.newEnvelope(frequency)
            }
        }
    }

    override fun newEnvelope(frequency: Float): Envelope {
        val envelopes = getEnvelopes(frequency)
        val result = MultiplexEnvelope(envelopes)
        return result
    }
}