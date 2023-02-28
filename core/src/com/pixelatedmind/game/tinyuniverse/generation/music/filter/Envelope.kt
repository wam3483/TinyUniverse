package com.pixelatedmind.game.tinyuniverse.generation.music.filter

import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream

interface Envelope : FloatInputStream {
    fun release()

    fun isComplete() : Boolean

    fun removeReleaseListener(listener:()->Unit) : Boolean

    fun addReleaseListener(listener:()->Unit)
}