package com.pixelatedmind.game.tinyuniverse.screen.synthui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align

class SynthUIBuilder {

    var oscillatorMixChangeListener : ((Float) -> Unit)? = null
    var oscillatorChangeListener : ((Int, Oscillator) -> Unit)? = null

    fun buildUI() : Stage{
        val stage = Stage()
        val table = Table()
        table.setFillParent(true)
        stage.addActor(table)
//        table.setDebug(true)

        val skin = Skin(Gdx.files.internal("skins/neonui/neon-ui.json"))

        val o1Label = Label("Oscillator 1", skin)
        o1Label.setAlignment(Align.center)
        val oscillatorDropdown1 = SelectBox<Oscillator>(skin)
        oscillatorDropdown1.setItems(Oscillator.Sine, Oscillator.Pulse, Oscillator.Saw, Oscillator.Triangle)
        oscillatorDropdown1.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                oscillatorChangeListener?.invoke(0, oscillatorDropdown1.selected)
            }
        })

        val o2Label = Label("Oscillator 2", skin)
        o2Label.setAlignment(Align.center)
        val oscillatorDropdown2 = SelectBox<Oscillator>(skin)
        oscillatorDropdown2.setItems(Oscillator.Sine, Oscillator.Pulse, Oscillator.Saw, Oscillator.Triangle)
        oscillatorDropdown2.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                oscillatorChangeListener?.invoke(1, oscillatorDropdown2.selected)
            }
        })

        val mixSlider = Slider(0f, 1f, .01f,false,skin)
        val o1MixLabel = Label("O1 100%", skin)
        val o2MixLabel = Label("O2 0%", skin)
        o1MixLabel.setAlignment(Align.center)
        o2MixLabel.setAlignment(Align.center)
        mixSlider.addListener( object : ChangeListener(){
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val mixBalance = (mixSlider.value*100).toInt()
                o2MixLabel.setText("O2 ${mixBalance}%")
                o1MixLabel.setText("O1 ${(100 - mixBalance)}%")
                oscillatorMixChangeListener?.invoke(mixSlider.value)
            }
        })
        table.add(o1Label)
        table.add(oscillatorDropdown1)
        table.row()
        table.add(o2Label)
        table.add(oscillatorDropdown2)

        table.row()
        table.add()
        table.add(Label("Mix", skin)).bottom().center()
        table.row()
        table.add(o1MixLabel).width(100f)
        table.add(mixSlider)
        table.add(o2MixLabel).width(100f)

        table.left().top()
        return stage
    }
}