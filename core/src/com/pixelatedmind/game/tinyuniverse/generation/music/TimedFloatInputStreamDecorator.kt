package com.pixelatedmind.game.tinyuniverse.generation.music

class TimedFloatInputStreamDecorator(var stream : FloatInputStream) : FloatInputStream {
    private var epoc : Float? = null
    override fun read(timeInSeconds: Float): Float {
        if(epoc == null){
            epoc = timeInSeconds
        }
        return stream.read(timeInSeconds - epoc!!)
    }
}