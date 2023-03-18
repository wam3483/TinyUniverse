package com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.inputs

import com.badlogic.gdx.math.Interpolation
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

///Ping pong controls whether animation loops from start->end->start->end, or from start->end, then restarting suddenly at start
///it's likely most the time pingpong will be set to true.
class AnimatedValueImpl(val animationDurationSecs : Float,
                        val start : Float = 0f,
                        val end : Float = 1f,
                        val interpolation : Interpolation = Interpolation.linear,
                        val timesToRepeat : Int = -1,
                        val pingPong : Boolean = true) : FloatInputStream {

    override fun read(timeElapsed: Float): Float {
        val animationIterations = (timeElapsed / animationDurationSecs).toInt()
        if(timesToRepeat == -1 || animationIterations <= timesToRepeat) {
            var frameDuration = animationDurationSecs
            var currentMin = start
            var currentMax = end
            if(pingPong){
                frameDuration = animationDurationSecs / 2
                val isAnimationDescending = timeElapsed / animationDurationSecs > .5f
                println("animated value: "+isAnimationDescending)
                if(isAnimationDescending){
                    currentMin = end
                    currentMax = start
                }
            }
            val animationFrameAlpha = (timeElapsed % frameDuration) / frameDuration
            return interpolation.apply(currentMin, currentMax, animationFrameAlpha)
        }
        return interpolation.apply(start, end, 1f)
    }
}