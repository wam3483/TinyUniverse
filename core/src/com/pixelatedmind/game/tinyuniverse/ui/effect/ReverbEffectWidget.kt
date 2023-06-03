package com.pixelatedmind.game.tinyuniverse.ui.effect

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.spinner.FloatSpinnerModel
import com.kotcrab.vis.ui.widget.spinner.Spinner
import com.pixelatedmind.game.tinyuniverse.ui.EnvelopeRepository
import com.pixelatedmind.game.tinyuniverse.ui.events.DeleteEffectRequest
import com.pixelatedmind.game.tinyuniverse.ui.model.ReverbModel
import com.pixelatedmind.game.tinyuniverse.util.EventBus

class ReverbEffectWidget(private val model : ReverbModel, private val piecewiseModelRepo : EnvelopeRepository, skin : Skin, private val eventBus : EventBus) : VisTable() {
    private val padding = 5f
    private var inEditMode = false
    init {
        this.skin = skin

        val txtName = TextField(model.name, skin)
        val btnEdit = TextButton("Edit", skin)
        btnEdit.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                inEditMode = !inEditMode
                if (inEditMode) {
                    btnEdit.setText("Save")
                } else {
                    btnEdit.setText("Edit")
                    model.name = txtName.text
                }
            }
        })

        val btnDelete = TextButton("Delete",skin)
        btnDelete.addListener(object : ClickListener(){
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                eventBus.post(DeleteEffectRequest(model))
                remove()
            }
        })

        val decaySpinnerModel = FloatSpinnerModel("${model.decay}","0",".99", ".01")
        val decaySpinner = Spinner("", decaySpinnerModel)

        val decaySlider = Slider(0f, .99f, .05f,false, skin)
        decaySlider.value = model.decay
        decaySlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                model.decay = decaySlider.value
                decaySpinnerModel.value = decaySlider.value.toBigDecimal()
            }
        })
        decaySpinner.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                model.decay = decaySpinnerModel.value.toFloat()
                decaySlider.value = decaySpinnerModel.value.toFloat()
            }
        })

        val delaySpinnerModel = FloatSpinnerModel("${model.delay}","0","10", ".1")
        val delaySpinner = Spinner("", delaySpinnerModel)

        delaySpinner.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                model.delay = delaySpinnerModel.value.toFloat()
            }
        })

        val headerTable = Table()
        headerTable.left()
        headerTable.add(Label("Name:",skin)).padRight(padding).padLeft(padding)
        headerTable.add(txtName).growX().padRight(padding)
        headerTable.add(btnEdit).padRight(padding)
        headerTable.add(btnDelete).padRight(padding)
        add(headerTable).growX().padTop(padding)
        row()

        val delayTable = Table()
        delayTable.add(Label("Delay:",skin)).padRight(padding)
        delayTable.add(delaySpinner).growX().padRight(padding)
        add(delayTable).growX().padTop(padding)
        row()

        val decayTable = Table()
        decayTable.add(Label("Decay:", skin)).padRight(padding)
        decayTable.add(decaySlider).growX().padRight(padding)
        decayTable.add(decaySpinner).padRight(padding)
        add(decayTable).growX().padTop(padding)
        row()

    }
}