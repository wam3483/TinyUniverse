package com.pixelatedmind.game.tinyuniverse.ui

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.util.adapter.AbstractListAdapter
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.util.dialog.Dialogs.OptionDialogType
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter
import com.kotcrab.vis.ui.widget.*
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel
import com.kotcrab.vis.ui.widget.spinner.SimpleFloatSpinnerModel
import com.kotcrab.vis.ui.widget.spinner.Spinner
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.ConstantStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.StreamFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform.SineWaveform
import com.pixelatedmind.game.tinyuniverse.generation.music.utils.ArrayUtils
import com.pixelatedmind.game.tinyuniverse.ui.dialogs.DialogResult
import com.pixelatedmind.game.tinyuniverse.ui.dialogs.EffectDialog
import com.pixelatedmind.game.tinyuniverse.ui.dialogs.FunctionSelectorDialog
import com.pixelatedmind.game.tinyuniverse.ui.events.AddEffectToOscillatorRequest
import com.pixelatedmind.game.tinyuniverse.ui.events.DeleteOscillatorRequest
import com.pixelatedmind.game.tinyuniverse.ui.events.OscillatorUI_CRUDEventHandler
import com.pixelatedmind.game.tinyuniverse.ui.events.RemoveEffectFromOscillatorRequest
import com.pixelatedmind.game.tinyuniverse.ui.model.EffectRef
import com.pixelatedmind.game.tinyuniverse.ui.model.NameableId
import com.pixelatedmind.game.tinyuniverse.ui.model.OscillatorModel
import com.pixelatedmind.game.tinyuniverse.ui.model.Range
import com.pixelatedmind.game.tinyuniverse.util.EventBus
import java.util.*


class OscilatorPanel(var model : OscillatorModel, val waveformFactory : StreamFactory,
                     val piecewiseModelRepo : EnvelopeRepository,
                     val effectRepository: EffectRepository,
                     val eventBus : EventBus) : VisTable(true) {

    var gainChangeListener : ((Float) -> Unit)? = null
    var oscillatorChangeListener : ((String) -> Unit)? = null
    var semitoneChangeListener : ((Int) -> Unit)? = null
    var finetuneChangeListener : ((Int) -> Unit)? = null
    var unisonChangeListener : ((Int) -> Unit)? = null

    private val waveformsSelectbox : VisSelectBox<String>
    private val deleteBtn : TextButton
    private val enableBtn : TextButton
    private val semitonesSpinner : Spinner
    private val finetuneSpinner : Spinner
    private val squareWaveDutyCycleSpinner : Spinner
    private val squareWaveDutyCycleBtn : EnvelopeButton

    private val dutyCycleTable = Table()
    private val dutyCycleTableCell : Cell<*>

    private val waveformPanel : WavetableDisplayPlanel
    private val unisonSpinner : Spinner
    private val gainSlider : Slider
    val semitonesBtn : EnvelopeButton
    val finetuneBtn : EnvelopeButton
    val gainBtn : EnvelopeButton
    
    init{

        val effectListAdapter = EffectRefListViewAdapter()
        val editEnvelopeListView =  ListView<EffectRef>(effectListAdapter)
        effectListAdapter.selectionMode = AbstractListAdapter.SelectionMode.MULTIPLE
        val crudUIEventHandler = OscillatorUI_CRUDEventHandler(model, effectListAdapter)
        eventBus.register(crudUIEventHandler::onEffectRemovedFromOscillator)
        eventBus.register(crudUIEventHandler::onEffectAddedToOscillator)

        piecewiseModelRepo.addModelDeletedListsener(this::piecewiseModelDeleted)

        waveformPanel = WavetableDisplayPlanel()
        waveformPanel.height = 100f
        waveformPanel.waveform = SineWaveform(ConstantStream(1f), 44100)

        waveformsSelectbox = VisSelectBox()
        waveformsSelectbox.items = ArrayUtils.toArray(waveformFactory.getWaveformIds())
        waveformsSelectbox.selected = model.baseWaveformId
        waveformsSelectbox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val selectedWaveform = waveformsSelectbox.selected
                val showDutyCycle = selectedWaveform == "square"
                showDutyCycleOptions(showDutyCycle)
                waveformPanel.waveform = waveformFactory.new(selectedWaveform, null, ConstantStream(1f))
                model.baseWaveformId = waveformsSelectbox.selected
                oscillatorChangeListener?.invoke(waveformsSelectbox.selected)
            }
        })

        val squareWaveDutyCycleLink = LinkLabel("Duty Cycle:","")
        squareWaveDutyCycleLink.setListener {
            url->
            println("clicked squareWaveDutyCycleLink")
        }
        squareWaveDutyCycleBtn = EnvelopeButton(model.dutyCycleEnv,"", skin)
        squareWaveDutyCycleBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                showEnvelopeSelectionDialog("Duty Cycle", {piecewiseModel->
                    model.dutyCycleEnv = piecewiseModel
                    squareWaveDutyCycleBtn.setPiecewiseFunction(piecewiseModel)
                })
            }
        })
        val squareWaveSpinnerModel = SimpleFloatSpinnerModel(model.dutyCycle,0f, 1f, .1f)
        squareWaveDutyCycleSpinner = Spinner("", squareWaveSpinnerModel)
        squareWaveDutyCycleSpinner.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                model.dutyCycle = squareWaveSpinnerModel.value
            }
        })

        deleteBtn = TextButton("Delete", skin)
        deleteBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val dialog = Dialogs.showOptionDialog(stage, "Delete Oscillator?", "Proceed with deleting oscillator?", OptionDialogType.YES_NO_CANCEL, object : OptionDialogAdapter() {
                    override fun yes() {
                        eventBus.post(DeleteOscillatorRequest(model))
                    }
                })
                dialog.addCloseButton()
                dialog.closeOnEscape()
            }
        })

        enableBtn = TextButton("On", skin)
        enableBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                model.enabled = !model.enabled
                if(model.enabled){
                    enableBtn.setText("On")
                }else{
                    enableBtn.setText("Off")
                }
                layout()
            }
        })

        semitonesBtn = EnvelopeButton(null,"", VisUI.getSkin())
        semitonesBtn.setPiecewiseFunction(model.semitoneOffsetEnv)
        semitonesBtn.addListener(object : ClickListener(){
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                showToneSelectionDialog()
            }
        })
        val semitonesLinkLabel = LinkLabel("Semi:","")
        semitonesLinkLabel.setListener {
            url->showToneSelectionDialog()
        }
        semitonesSpinner = Spinner("", IntSpinnerModel(model.semitoneOffset,-144, 144, 1))
        semitonesSpinner.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val intModel = semitonesSpinner.model as IntSpinnerModel
                debug("semitone changed : "+intModel.value)
                model.semitoneOffset = intModel.value
                semitoneChangeListener?.invoke(intModel.value)
            }
        })

        finetuneBtn = EnvelopeButton(null,"", VisUI.getSkin())
        finetuneBtn.setPiecewiseFunction(model.fineTuneCentOffsetEnv)
        finetuneBtn.addListener(object : ClickListener(){
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val models = piecewiseModelRepo.getAllModels()
                if (models.none()){
                    showNoEnvelopesDialog()
                }else {
                    val dialog = FunctionSelectorDialog.IntBoundFunctionDialog("Cents Offset", skin,stage,
                            piecewiseModelRepo.getAllModels(),
                            "Starting cents offset:",
                            "Ending cents offset:",
                            0, -100, 100, 1
                    , { dialogModel, dialogResult ->
                        if (dialogResult == DialogResult.Accept) {
                            finetuneBtn.setPiecewiseFunction(dialogModel!!.model)
                            model.fineTuneCentOffsetEnv = dialogModel.model
                        } else if (dialogResult == DialogResult.Clear) {
                            finetuneBtn.setPiecewiseFunction(null)
                            model.fineTuneCentOffsetEnv = null
                        }
                    })
                    stage.addActor(dialog.fadeIn())
                }
            }
        })

        finetuneBtn.width = 25f
        finetuneBtn.height = 25f
        val finetuneLinkLabel = LinkLabel("Fine:","")
        finetuneLinkLabel.setListener {
            url->
            println("clicked finetune")
        }
        finetuneSpinner = Spinner("", IntSpinnerModel(model.fineTuneCentOffset,-100, 100, 1))
        finetuneSpinner.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val intModel = finetuneSpinner.model as IntSpinnerModel
                debug("finetune changed : "+intModel.value)
                model.fineTuneCentOffset = intModel.value
                finetuneChangeListener?.invoke(intModel.value)
            }
        })

        gainBtn = EnvelopeButton(null,"", VisUI.getSkin())
        val gainLinkLabel = LinkLabel("Gain:","")
        gainLinkLabel.setListener {
            url->
            println("clicked gain link label")
        }
        gainBtn.setPiecewiseFunction(model.amplitudeEnv)
        gainSlider = Slider(0f, 1f, .01f, false, skin)
        gainSlider.value = model.amplitude
        gainSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                debug("gain changed : "+gainSlider.value)
                model.amplitude = gainSlider.value
                gainChangeListener?.invoke(gainSlider.value)
            }
        })

        gainBtn.addListener(object : ClickListener(){
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                showEnvelopeSelectionDialog("Amp Envelope", {func->
                    gainBtn.setPiecewiseFunction(func)
                    model.amplitudeEnv = func
                    if(model.amplitudeEnv!=null) {
                        model.amplitudeEnv!!.startY = 0f
                        model.amplitudeEnv!!.endY = 1f
                    }
                })
            }
        })

        unisonSpinner = Spinner("", IntSpinnerModel(model.unisonVoices,0, 10, 1))
        unisonSpinner.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val intModel = unisonSpinner.model as IntSpinnerModel
                debug("unison changed : "+intModel.value)
                model.unisonVoices = intModel.value
                unisonChangeListener?.invoke(intModel.value)
            }
        })

        val btnAddEffect = TextButton("Add effect", skin)
        btnAddEffect.addListener(object : ClickListener(){
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val effectDialog = EffectDialog("Select Effect", effectRepository,{selection->
                    selection.filter{!model.effectIds.any{effect->effect.nameId == it}}.forEach{effectId ->
                        eventBus.post(AddEffectToOscillatorRequest(model, EffectRef(effectId,true)))
                    }
                })
                stage.addActor(effectDialog)
            }
        })

        val btnDeleteEffect = TextButton("Delete effect", skin)
        btnDeleteEffect.isDisabled = true
        btnDeleteEffect.addListener(object : ClickListener(){
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                effectListAdapter.selection.forEach {
                    eventBus.post(RemoveEffectFromOscillatorRequest(it, model))
                }
            }
        })
        effectListAdapter.setItemClickListener({item->
            val selection = effectListAdapter.selection
            btnDeleteEffect.isDisabled = selection.none()
        })

        val waveformSelectRow = Table()
        waveformSelectRow.add(VisLabel("Form:")).padLeft(5f).padRight(5f)
        waveformSelectRow.add(waveformsSelectbox).growX().padRight(5f)

        dutyCycleTable.add(squareWaveDutyCycleLink).padRight(5f)
        dutyCycleTable.add(squareWaveDutyCycleBtn).padRight(5f)
        dutyCycleTable.add(squareWaveDutyCycleSpinner).padRight(5f)
        dutyCycleTableCell = waveformSelectRow.add(dutyCycleTable)
        waveformSelectRow.add(deleteBtn).padRight(5f)
        waveformSelectRow.add(enableBtn).width(50f)
        waveformSelectRow.left()
        add(waveformSelectRow).left().growX()
        row()

        val semitonesRow = Table()
        semitonesRow.padLeft(5f).padRight(5f)
        semitonesRow.add(semitonesLinkLabel).padRight(5f)
        semitonesRow.add(semitonesBtn).padRight(5f)
        semitonesRow.add(semitonesSpinner).growX().padRight(20f)

        semitonesRow.add(finetuneLinkLabel).padRight(5f)
        semitonesRow.add(finetuneBtn).padRight(5f)
        semitonesRow.add(finetuneSpinner).growX()
        add(semitonesRow).left().growX()
        row()

        add(waveformPanel).grow().left()
        row()

        val bottomRow = Table()
        bottomRow.padLeft(5f).padRight(5f)
        bottomRow.add(VisLabel("Unison:")).growX().padRight(5f)
        bottomRow.add(unisonSpinner).growX().padRight(20f)
        bottomRow.add(gainLinkLabel).growX().padRight(5f)
        bottomRow.add(gainBtn).padRight(5f)
        bottomRow.add(gainSlider).growX()
        add(bottomRow).left().growX()
        row()

        val effectTable = Table()
        val effectAddDeleteTable = Table()
        effectAddDeleteTable.add(btnAddEffect).growX().padRight(5f).padLeft(5f)
        effectAddDeleteTable.add(btnDeleteEffect).growX().padRight(5f)
        effectTable.add(effectAddDeleteTable).growX()
        effectTable.row()

        effectTable.add(editEnvelopeListView.mainTable).growX().height(100f)
        add(effectTable).left().growX()

        showDutyCycleOptions(false)

        invalidate()
        invalidateHierarchy()
        layout()
    }

    val spacing = 5f
    private fun showDutyCycleOptions(visibility: Boolean){
        dutyCycleTable.isVisible = visibility
        if(visibility) {
            dutyCycleTableCell.width(dutyCycleTable.prefWidth).padRight(spacing)
        }else{
            dutyCycleTableCell.width(0f).padRight(0f)
        }
        dutyCycleTable.invalidateHierarchy()
        pack()
        invalidateHierarchy()
    }

    private fun showEnvelopeSelectionDialog(title : String, callback : (PiecewiseModel?)->Unit){
        val models = piecewiseModelRepo.getAllModels()
        if (models.none()) {
            showNoEnvelopesDialog()
        }
        else {
            val dialog = FunctionSelectorDialog.SelectorDialog(title, skin, piecewiseModelRepo.getAllModels(),
            { dialogModel, dialogResult ->
                if (dialogResult == DialogResult.Accept) {
                    if(dialogModel==null) {
                        val okDialog = Dialogs.showOKDialog(stage, "Missing Envelope", "Must select an envelope.")
                        okDialog.addCloseButton()
                        okDialog.closeOnEscape()
                        okDialog.addListener(object : ChangeListener() {
                            override fun changed(event: ChangeEvent?, actor: Actor?) {
                                showEnvelopeSelectionDialog(title, callback)
                            }
                        })
                        stage.addActor(okDialog)
                    }else {
                        callback.invoke(dialogModel.model)
                    }
                } else if (dialogResult == DialogResult.Clear) {
                    gainBtn.setPiecewiseFunction(null)
                    callback.invoke(null)
                }
            })
            stage.addActor(dialog.fadeIn())
        }
    }

    private fun showToneSelectionDialog(){
        val models = piecewiseModelRepo.getAllModels()
        if (models.none()){
            showNoEnvelopesDialog()
        }else {
            val minStart = model.semitoneEnvelopeRange?.min ?: 0
            val maxStart = model.semitoneEnvelopeRange?.max ?: 0
            var dialogWindow : VisWindow? = null
            val dialog = FunctionSelectorDialog.IntBoundFunctionDialog("Semitone Offset", skin,stage,
                    models,
                    "Starting semitone offset:",
                    "Ending semitone offset:",
                    0, -100, 100, 1,
             { dialogModel, dialogResult ->
                if (dialogResult == DialogResult.Accept) {
                    if(dialogModel==null) {
                        val okDialog = Dialogs.showOKDialog(stage, "Missing Envelope", "Must select an envelope to continue")
                        okDialog.addCloseButton()
                        okDialog.closeOnEscape()
                        okDialog.fadeIn()
                        okDialog.addListener(object : ChangeListener() {
                            override fun changed(event: ChangeEvent?, actor: Actor?) {
//                                showToneSelectionDialog()
                            }
                        })
                        dialogWindow!!.remove()
                        stage.addActor(okDialog)
                    }else {
                        semitonesBtn.setPiecewiseFunction(dialogModel.model)
                        model.semitoneEnvelopeRange = Range(dialogModel.min, dialogModel.max)
                        model.semitoneOffsetEnv = dialogModel.model
                    }
                } else if (dialogResult == DialogResult.Clear) {
                    semitonesBtn.setPiecewiseFunction(null)
                    model.semitoneOffsetEnv = null
                }
            },minStart, maxStart)
            dialogWindow = dialog
            dialog.closeOnEscape()
            stage.addActor(dialog.fadeIn())
        }
    }

    private fun showNoEnvelopesDialog(){
        Dialogs.showOKDialog(stage, "Envelope selection", "No envelopes exist. Must create an envelope first to automate this field.")
                .addCloseButton()
    }

    private fun piecewiseModelDeleted(piecewise : PiecewiseModel){
        if(model.amplitudeEnv?.name == piecewise.name){
            gainBtn.setPiecewiseFunction(null)
            model.amplitudeEnv = null
        }
        if(model.fineTuneCentOffsetEnv?.name == piecewise.name){
            finetuneBtn.setPiecewiseFunction(null)
            model.fineTuneCentOffsetEnv = null
        }
        if(model.semitoneOffsetEnv?.name == piecewise.name){
            semitonesBtn.setPiecewiseFunction(null)
            model.semitoneOffsetEnv = null
        }
    }

    override fun setBounds(x: Float, y: Float, width: Float, height: Float) {
        super.setBounds(x, y, width, height)
        //I cannot figure out how to get layouts to trigger to this element.
        //hack around
        waveformPanel.x = x
        waveformPanel.width = width
    }

    fun buildPiecewiseModel() : PiecewiseModel{
        val random = Random()
        val model =
                PiecewiseModel(listOf<PiecewiseModel.Piece>(
                        PiecewiseModel.Piece(Vector2(0f, random.nextFloat()), "sine", Interpolation.sine),
                        PiecewiseModel.Piece(Vector2(.25f+random.nextFloat()*.2f, .6f), "sine", Interpolation.sine),
                        PiecewiseModel.Piece(Vector2(.5f+random.nextFloat()*.25f, .8f), "sine", Interpolation.sine),
                        PiecewiseModel.Piece(Vector2(1f, 0f), "linear", Interpolation.linear)
                ))
        model.name = "test"
        return model
    }
    private fun debug(s : String){
        println(s)
    }
}