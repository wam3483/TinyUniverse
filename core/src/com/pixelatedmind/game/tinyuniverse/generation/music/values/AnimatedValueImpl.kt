package com.pixelatedmind.game.tinyuniverse.generation.music.values

import com.badlogic.gdx.math.Interpolation
///Ping pong controls whether animation loops from start->end->start->end, or from start->end, then restarting suddenly at start
///it's likely most the time pingpong will be set to true.
class AnimatedValueImpl(val animationDurationSecs : Float,
                        val min : Float = 0f,
                        val max : Float = 1f,
                        val interpolation : Interpolation = Interpolation.linear,
                        val timesToRepeat : Int = -1,
                        val pingPong : Boolean = true) : AnimatedValue {

    override fun getValue(timeElapsed: Float): Float {
        val animationIterations = timeElapsed / animationDurationSecs
        if(timesToRepeat == -1 || animationIterations <= timesToRepeat) {
            var frameDuration = animationDurationSecs
            var currentMin = min
            var currentMax = max
            if(pingPong){
                frameDuration = animationDurationSecs / 2
                val isAnimationDescending = timeElapsed / animationDurationSecs > .5f
                println("animated value: "+isAnimationDescending)
                if(isAnimationDescending){
                    currentMin = max
                    currentMax = min
                }
            }
            val animationFrameAlpha = timeElapsed % frameDuration / frameDuration
            return interpolation.apply(currentMin, currentMax, animationFrameAlpha)
        }
        return interpolation.apply(min, max, 1f)
    }
}