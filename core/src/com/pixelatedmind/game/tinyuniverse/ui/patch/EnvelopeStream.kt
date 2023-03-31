package com.pixelatedmind.game.tinyuniverse.ui.patch

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.AbstractEnvelope
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

class EnvelopeStream(val stream : FloatInputStream) : AbstractEnvelope() {
    private var released : Boolean = false
    private var complete : Boolean = false
    open override fun release() {
        if(!released){
            released = true
            this.fireReleaseEvent()
        }
        released = true
    }

    fun setIsComplete(complete : Boolean){
        this.complete = complete
    }

    open override fun isComplete(): Boolean {
        return complete
    }

    override fun read(timeInSeconds: Float): Float {
        val result = stream.read(timeInSeconds)
        return result
    }
}