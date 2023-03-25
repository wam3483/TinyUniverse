package com.pixelatedmind.game.tinyuniverse.services

import com.badlogic.gdx.math.Interpolation

class InterpolationFactory {
    private val allInterpolations = listOf(
            "linear",
            "bounce","bounceIn","bounceOut",
            "circle","circleIn","circleOut",
            "elastic","elasticIn","elasticOut",
            "exp10","exp10In","exp10Out",
            "exp5","exp5In",
            "fade","fastSlow","slowFast",
            "pow2","pow2In", "pow2InInverse", "pow2Out","pow2OutInverse",
            "pow3","pow3In","pow3InInverse","pow3Out","pow3OutInverse",
            "pow4", "pow4In","pow4Out",
            "pow5","pow5In","pow5Out",
            "sine","sineIn","sineOut",
            "smooth","smooth2","smoother",
            "swing","swingIn","swingOut"
    )

    private val interpolationMap = mapOf(
            "linear" to Interpolation.linear,
            "bounce" to Interpolation.bounce,"bounceIn" to Interpolation.bounceIn,"bounceOut" to Interpolation.bounceOut,
            "circle" to Interpolation.circle,"circleIn" to Interpolation.circleIn,"circleOut" to Interpolation.circleOut,
            "elastic" to Interpolation.elastic,"elasticIn" to Interpolation.elasticIn,"elasticOut" to Interpolation.elasticOut,
            "exp10" to Interpolation.exp10,"exp10In" to Interpolation.exp10In,"exp10Out" to Interpolation.exp10Out,
            "exp5" to Interpolation.exp5,"exp5In" to Interpolation.exp5In,
            "fade" to Interpolation.fade,"fastSlow" to Interpolation.fastSlow,"slowFast" to Interpolation.slowFast,
            "pow2" to Interpolation.pow2,"pow2In" to Interpolation.pow2In, "pow2InInverse" to Interpolation.pow2InInverse, "pow2Out" to Interpolation.pow2Out,"pow2OutInverse" to Interpolation.pow2OutInverse,
            "pow3" to Interpolation.pow3,"pow3In" to Interpolation.pow3In,"pow3InInverse" to Interpolation.pow3InInverse,"pow3Out" to Interpolation.pow3Out,"pow3OutInverse" to Interpolation.pow3OutInverse,
            "pow4" to Interpolation.pow4, "pow4In" to Interpolation.pow4In,"pow4Out" to Interpolation.pow4Out,
            "pow5" to Interpolation.pow5,"pow5In" to Interpolation.pow5In,"pow5Out" to Interpolation.pow5Out,
            "sine" to Interpolation.sine,"sineIn" to Interpolation.sineIn,"sineOut" to Interpolation.sineOut,
            "smooth" to Interpolation.smooth,"smooth2" to Interpolation.smooth2,"smoother" to Interpolation.smoother,
            "swing" to Interpolation.swing,"swingIn" to Interpolation.swingIn,"swingOut" to Interpolation.swingOut
    )

    fun allInterpolationNames() : List<String>{
        return allInterpolations
    }

    fun new(name : String) : Interpolation {
        val result = interpolationMap[name]
        return result!!
    }
}