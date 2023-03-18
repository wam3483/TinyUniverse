package com.pixelatedmind.game.tinyuniverse.generation.music.synth.filter

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.BufferedInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

class EqualizerStream(val inputStream : FloatInputStream, val filter : EqualizerFilter) : BufferedInputStream(inputStream,bufferSize=1024) {

    override fun nextBuffer(epoch: Float): FloatArray {
        val buffer = super.nextBuffer(epoch)
        filter.apply(buffer)
        return buffer
    }
}