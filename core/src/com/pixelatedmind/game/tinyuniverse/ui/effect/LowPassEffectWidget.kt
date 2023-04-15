package com.pixelatedmind.game.tinyuniverse.ui.effect

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.LinkLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.spinner.FloatSpinnerModel
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel
import com.kotcrab.vis.ui.widget.spinner.Spinner
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.filter.LowPassSlope
import com.pixelatedmind.game.tinyuniverse.generation.music.utils.ArrayUtils
import com.pixelatedmind.game.tinyuniverse.ui.EnvelopeButton
import com.pixelatedmind.game.tinyuniverse.ui.EnvelopeRepository
import com.pixelatedmind.game.tinyuniverse.ui.dialogs.DialogResult
import com.pixelatedmind.game.tinyuniverse.ui.dialogs.FunctionSelectorDialog
import com.pixelatedmind.game.tinyuniverse.ui.events.DeleteEffectRequest
import com.pixelatedmind.game.tinyuniverse.ui.model.LowPassModel
import com.pixelatedmind.game.tinyuniverse.ui.model.Range
import com.pixelatedmind.game.tinyuniverse.util.EventBus
import java.math.BigDecimal

class LowPassEffectWidget(val lowpassModel : LowPassModel, private val piecewiseModelRepo : EnvelopeRepository, skin : Skin, private val eventBus : EventBus) : VisTable() {
    private val padding = 5f
    private var inEditMode = false
    init{
        this.skin = skin

        val txtName = TextField(lowpassModel.name, skin)
        val btnEdit = TextButton("Edit", skin)
        btnEdit.addListener(object : ClickListener(){
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                inEditMode = !inEditMode
                if(inEditMode){
                    btnEdit.setText("Save")
                }else{
                    btnEdit.setText("Edit")
                    lowpassModel.name = txtName.text
                }
            }
        })

        val btnDelete = TextButton("Delete",skin)
        btnDelete.addListener(object : ClickListener(){
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                eventBus.post(DeleteEffectRequest(lowpassModel))
                remove()
            }
        })

        val slopeCmbBox = SelectBox<LowPassSlope>(skin)
        slopeCmbBox.items = ArrayUtils.toArray(LowPassSlope.values().toList())
        slopeCmbBox.selected = lowpassModel.slope
        slopeCmbBox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val slope = slopeCmbBox.selected
                if(slope!=null){
                    lowpassModel.slope = slope
                }
            }
        })

        val resonanceSpinnerModel = FloatSpinnerModel("${lowpassModel.resonance}","0","10", ".01")
        val resonanceSpinner = Spinner("", resonanceSpinnerModel)
        resonanceSpinnerModel.value = BigDecimal(lowpassModel.resonance.toDouble())
        resonanceSpinner.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                lowpassModel.resonance = resonanceSpinnerModel.value.toFloat()
            }
        })

        val cutoffSpinnerModel = IntSpinnerModel(lowpassModel.cutoffFrequency.toInt(),0, 16000, 25)
        val cutoffSpinner = Spinner("", cutoffSpinnerModel)
        cutoffSpinnerModel.value = lowpassModel.cutoffFrequency.toInt()
        cutoffSpinner.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                lowpassModel.cutoffFrequency = cutoffSpinnerModel.value.toFloat()
            }
        })

        val cutoffEnvBtn = EnvelopeButton(lowpassModel.cutoffFrequencyStream,"", VisUI.getSkin())
        cutoffEnvBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                showCutoffFrequencyEnvelopeSelector(cutoffEnvBtn)
            }
        })

        val resonanceEnvBtn = EnvelopeButton(lowpassModel.resonanceStream,"", VisUI.getSkin())
        resonanceEnvBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                showEnvelopeSelector(resonanceEnvBtn)
            }
        })

        val resonanceLink = LinkLabel("Resonance:","")
        resonanceLink.setListener {
            url -> showEnvelopeSelector(resonanceEnvBtn)
        }

        val cutoffFreqLink = LinkLabel("Cutoff Frequency:","")
        cutoffFreqLink.setListener {
            url -> showCutoffFrequencyEnvelopeSelector(cutoffEnvBtn)
        }

        val overdriveMax = 10f
        val overdriveStep = .1f
        val overdriveSpinnerModel = FloatSpinnerModel("${lowpassModel.overdrive}","1","$overdriveMax", "$overdriveStep")
        val overdriveSlider = Slider(1f, overdriveMax, overdriveStep,false, skin)
        overdriveSlider.value = lowpassModel.overdrive
        overdriveSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                overdriveSpinnerModel.value = overdriveSlider.value.toBigDecimal()
                lowpassModel.overdrive = overdriveSlider.value
            }
        })

        val overdriveSpinner = Spinner("", overdriveSpinnerModel)
        overdriveSpinner.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                overdriveSlider.value = overdriveSpinnerModel.value.toFloat()
                lowpassModel.overdrive = overdriveSlider.value
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

        val slopeTable = Table()
        slopeTable.left()
        slopeTable.add(Label("Slope:", skin)).padLeft(padding)
        slopeTable.add(slopeCmbBox).padRight(padding).padLeft(padding).padTop(padding)
                .growX()
        add(slopeTable).growX()
        row()

        val resonanceTable = Table()
        resonanceTable.left()
        resonanceTable.add(resonanceLink).padRight(padding).padLeft(padding)
        resonanceTable.add(resonanceEnvBtn).padRight(padding)
        resonanceTable.add(resonanceSpinner).growX().padRight(padding)
        add(resonanceTable).growX().padTop(padding)
        row()

        val cutoffFrequencyTable = Table()
        cutoffFrequencyTable.left()
        cutoffFrequencyTable.add(cutoffFreqLink).padRight(padding).padLeft(padding)
        cutoffFrequencyTable.add(cutoffEnvBtn).padRight(padding)
        cutoffFrequencyTable.add(cutoffSpinner).growX().padRight(padding)
        add(cutoffFrequencyTable).growX().padTop(padding)
        row()

        val overdriveTable = Table()
        overdriveTable.left()
        overdriveTable.add(Label("Overdrive:",skin)).padRight(padding).padLeft(padding)
        overdriveTable.add(overdriveSlider).growX().padRight(padding)
        overdriveTable.add(overdriveSpinner).padRight(padding)
        add(overdriveTable).growX().padTop(padding)
        row()

        left()
    }

    private fun showCutoffFrequencyEnvelopeSelector(cutoffEnvBtn : EnvelopeButton){
        val maxStartValue = lowpassModel.cutoffFrequencytStreamRange?.max ?: 0
        val minStartValue = lowpassModel.cutoffFrequencytStreamRange?.min ?: 0
        FunctionSelectorDialog.IntBoundFunctionDialog("Cutoff Frequency",skin,stage,piecewiseModelRepo.getAllModels(),
                "Start cutoff frequency:","Max cutoff frequency",0,0,16000,25,{dialogModel, dialogResult->
            when(dialogResult){
                DialogResult.Clear-> {
                    cutoffEnvBtn.setPiecewiseFunction(null)
                    lowpassModel.cutoffFrequencyStream = null
                }
                DialogResult.Accept->
                    if(dialogModel!=null){
                        cutoffEnvBtn.setPiecewiseFunction(dialogModel.model)
                        lowpassModel.cutoffFrequencytStreamRange = Range(dialogModel.min, dialogModel.max)
                        lowpassModel.cutoffFrequencyStream = dialogModel.model
                    }
            }
        }, minStartValue, maxStartValue)
    }

    private fun showEnvelopeSelector(envButton : EnvelopeButton){
        val dialog = FunctionSelectorDialog.SelectorDialog("Resonance Envelope",skin,piecewiseModelRepo.getAllModels(),
                {model,dialogResult->
                    when(dialogResult){
                        DialogResult.Clear-> {
                            envButton.setPiecewiseFunction(null)
                            lowpassModel.resonanceStream = null
                        }
                        DialogResult.Accept->
                            if(model!=null){
                                envButton.setPiecewiseFunction(model.model)
                                lowpassModel.resonanceStream = model.model
                            }
                    }
                }
        )
        stage.addActor(dialog)
    }
}