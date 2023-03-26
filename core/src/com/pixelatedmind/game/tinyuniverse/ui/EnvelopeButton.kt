package com.pixelatedmind.game.tinyuniverse.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.utils.Align

class EnvelopeButton(function : PiecewiseModel?,text : String, skin : Skin) : Button() {
    private val label : Label
    private val piecewiseActor : PiecewiseFunctionActor

    init{
        piecewiseActor = PiecewiseFunctionActor(function)
        piecewiseActor.debug = false
        val buttonStyle = skin[TextButtonStyle::class.java]
        style = buttonStyle
        label = Label(text, LabelStyle(buttonStyle!!.font, buttonStyle!!.fontColor))
        label.setAlignment(Align.left)
        piecewiseActor.width = 25f
        piecewiseActor.height = 25f
        add(piecewiseActor).pad(7f)
        add(label).expand().fill()
        setSize(prefWidth, prefHeight)
    }

    fun getPiecewiseFunction() : PiecewiseModel?{
        return piecewiseActor.getPiecewise()
    }
    fun setPiecewiseFunction(model : PiecewiseModel?){
        piecewiseActor.setPiecewise(model)
    }
}