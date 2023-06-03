package com.pixelatedmind.game.tinyuniverse.animation

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.generation.music.song.model.NoteLength
import com.pixelatedmind.game.tinyuniverse.util.SimpleTimer
import space.earlygrey.shapedrawer.ShapeDrawer

class CRTPowerOnAnimation(width : Float, height : Float, val drawer : ShapeDrawer) {
    private val model = CRTPowerOnModel(width, height)
    fun render(delta : Float){
        model.update(delta)
        drawer.batch.begin()
        drawer.setColor(model.color)
        drawer.filledRectangle(model.entityBounds)
        drawer.batch.end()
    }
}
class CRTPowerOnModel(val width : Float, val height : Float){
    private val startColor = Color.BLACK
    private val endColor = Color.BLACK
    var color = Color.WHITE
    private var phase = AnimationPhase.PowerOn
    private val powerOnDuration = .05f
    private val powerOnBoundsTarget = Vector2(width * 1/8f, height * 1/10f)
    private val powerOnInterpolation = Interpolation.linear
    private val expandHorizontalInterpolation = Interpolation.linear
    private val expandVerticalInterpolation = Interpolation.linear
    private val expandingHorizontalDuration = .125f
    private val expandingVerticalDuration = .1f
    val entityBounds = Rectangle()

    private var animationTimer = SimpleTimer(powerOnDuration, this::powerOnAnimationElapsed)

    fun update(elapsedSecs : Float){
        animationTimer.update(elapsedSecs)
        when(phase){
            AnimationPhase.PowerOn->{animatePowerOn()}
            AnimationPhase.ExpandingHorizontal->{animateExpandHorizontal()}
            AnimationPhase.ExpandingVertical->{animateExpandVertical()}
            AnimationPhase.FadeOut -> {animateFadeOut()}
        }
    }

    private fun animateFadeOut(){
        color = startColor.lerp(endColor, animationTimer.elapsedSecs() / animationTimer.duration)
    }

    private fun centerBounds(){
        entityBounds.x = width / 2 - entityBounds.width / 2
        entityBounds.y = height / 2 - entityBounds.height / 2
    }

    private fun animatePowerOn(){
        val elapsed = animationTimer.elapsedSecs()
        entityBounds.width = powerOnInterpolation.apply(0f, powerOnBoundsTarget.x,elapsed / animationTimer.duration)
        entityBounds.height = powerOnInterpolation.apply(0f, powerOnBoundsTarget.y, elapsed / animationTimer.duration)
        centerBounds()
    }

    private fun animateExpandHorizontal(){
        val elapsed = animationTimer.elapsedSecs()
        entityBounds.width = expandHorizontalInterpolation.apply(powerOnBoundsTarget.x, width, elapsed / animationTimer.duration)
        entityBounds.height = expandHorizontalInterpolation.apply(powerOnBoundsTarget.y, 5f, elapsed / animationTimer.duration)
        centerBounds()
    }

    private fun animateExpandVertical(){
        val elapsed = animationTimer.elapsedSecs()
        entityBounds.width = width
        entityBounds.height = expandVerticalInterpolation.apply(5f, height, elapsed / animationTimer.duration)
        centerBounds()
    }

    private fun powerOnAnimationElapsed(){
        animationTimer = SimpleTimer(expandingHorizontalDuration, this::expandingHorizontalCallback)
        phase = AnimationPhase.ExpandingHorizontal
    }

    private fun expandingHorizontalCallback(){
        animationTimer = SimpleTimer(expandingVerticalDuration, this::expandingVerticalCallback)
        phase = AnimationPhase.ExpandingVertical
    }

    private fun expandingVerticalCallback(){
        entityBounds.width = width
        entityBounds.height = height
        centerBounds()
        phase = AnimationPhase.FadeOut
        animationTimer = SimpleTimer(.5f, this::completeCallback)
    }

    private fun completeCallback(){
        animationTimer.enabled = false
        color = endColor
        phase = AnimationPhase.Complete
    }

    private enum class AnimationPhase{
        PowerOn,
        ExpandingHorizontal,
        ExpandingVertical,
        FadeOut,
        Complete
    }
}