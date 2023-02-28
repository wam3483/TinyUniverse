package com.pixelatedmind.game.tinyuniverse.generation.music.values

import com.badlogic.gdx.math.Interpolation
class InterpolatedNormalValue(val animationDurationSecs : Float,
                              val min : Float = 0f, val max : Float = 1f, val interpolation : Interpolation = Interpolation.linear) : NormalizedValue {
    var lastAlpha = 0f
    var startValue = min
    var endValue = max
    override fun getValue(timeElapsed: Float): Float {
        val alpha = (timeElapsed%animationDurationSecs)/animationDurationSecs
        if(lastAlpha > alpha){
            val temp = startValue
            startValue = endValue
            endValue = temp
        }
        lastAlpha = alpha
        return interpolation.apply(startValue, endValue, alpha)
    }
}