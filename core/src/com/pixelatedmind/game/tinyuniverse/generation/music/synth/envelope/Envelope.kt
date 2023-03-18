package com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

interface Envelope : FloatInputStream {
    fun release()

    fun isComplete() : Boolean

    fun removeReleaseListener(listener:()->Unit) : Boolean

    fun addReleaseListener(listener:()->Unit)
}