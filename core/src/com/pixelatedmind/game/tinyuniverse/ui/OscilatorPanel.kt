package com.pixelatedmind.game.tinyuniverse.ui

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.LinkLabel
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisSelectBox
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel
import com.kotcrab.vis.ui.widget.spinner.Spinner
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.BaseWaveformStreamFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.ConstantStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform.SineWaveform
import com.pixelatedmind.game.tinyuniverse.generation.music.utils.ArrayUtils
import com.pixelatedmind.game.tinyuniverse.screen.synthui.Oscillator
import com.pixelatedmind.game.tinyuniverse.ui.model.OscillatorModel
import java.util.*


class OscilatorPanel(val waveformFactory : BaseWaveformStreamFactory,
                     val piecewiseModelRepo : PiecewiseModelRepository) : VisTable(true) {

    var model : OscillatorModel

    var gainChangeListener : ((Float) -> Unit)? = null
    var oscillatorChangeListener : ((String) -> Unit)? = null
    var semitoneChangeListener : ((Int) -> Unit)? = null
    var finetuneChangeListener : ((Int) -> Unit)? = null
    var unisonChangeListener : ((Int) -> Unit)? = null

    private val waveformsSelectbox : VisSelectBox<String>
    private val semitonesSpinner : Spinner
    private val finetuneSpinner : Spinner
    private val waveformPanel : WavetableDisplayPlanel
    private val unisonSpinner : Spinner
    private val gainSlider : Slider

    init{
        model = OscillatorModel()
        piecewiseModelRepo.addModelDeletedListsener(this::piecewiseModelDeleted)

        waveformPanel = WavetableDisplayPlanel()
        waveformPanel.height = 100f
        waveformPanel.waveform = SineWaveform(ConstantStream(1f))

        waveformsSelectbox = VisSelectBox()
        waveformsSelectbox.items = ArrayUtils.toArray(waveformFactory.getBaseWaveformIds())
        waveformsSelectbox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                waveformPanel.waveform = waveformFactory.new(waveformsSelectbox.selected)
                model.baseWaveformId = waveformsSelectbox.selected
                oscillatorChangeListener?.invoke(waveformsSelectbox.selected)
            }
        })


        val semitonesBtn = EnvelopeButton(null,"", VisUI.getSkin())
        semitonesBtn.addListener(object : ClickListener(){
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val dialog = FunctionSelectorDialog.IntBoundFunctionDialog("Semitone Offset", skin,
                        piecewiseModelRepo.getAllModels(),
                        "Starting semitone offset:",
                        "Ending semitone offset:",
                        0,-100,100,1
                ) { model, dialogResult ->
                    if (dialogResult == DialogResult.Accept) {
                        semitonesBtn.setPiecewiseFunction(model!!.model)
                    } else if(dialogResult == DialogResult.Clear) {
                        semitonesBtn.setPiecewiseFunction(null)
                    }
                }
                dialog.addCloseButton()
                stage.addActor(dialog.fadeIn())
            }
        })
        val semitonesLinkLabel = LinkLabel("Semi:","")
        semitonesLinkLabel.setListener {
            url->
            println("clicked linklabel")
        }
        semitonesSpinner = Spinner("", IntSpinnerModel(0,-144, 144, 1))
        semitonesSpinner.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val intModel = semitonesSpinner.model as IntSpinnerModel
                debug("semitone changed : "+intModel.value)
                model.semitoneOffset = intModel.value
                semitoneChangeListener?.invoke(intModel.value)
            }
        })

        val finetuneBtn = EnvelopeButton(null,"", VisUI.getSkin())
        finetuneBtn.addListener(object : ClickListener(){
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val dialog = FunctionSelectorDialog.IntBoundFunctionDialog("Cents Offset", skin,
                       piecewiseModelRepo.getAllModels(),
                        "Starting cents offset:",
                        "Ending cents offset:",
                        0,-100,100,1
                ) { model, dialogResult ->
                    if (dialogResult == DialogResult.Accept) {
                        finetuneBtn.setPiecewiseFunction(model!!.model)
                    } else if(dialogResult == DialogResult.Clear) {
                        finetuneBtn.setPiecewiseFunction(null)
                    }
                }
                dialog.addCloseButton()
                stage.addActor(dialog.fadeIn())
            }
        })

        finetuneBtn.width = 25f
        finetuneBtn.height = 25f
        val finetuneLinkLabel = LinkLabel("Fine:","")
        finetuneLinkLabel.setListener {
            url->
            println("clicked finetune")
        }
        finetuneSpinner = Spinner("", IntSpinnerModel(0,-100, 100, 1))
        finetuneSpinner.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val intModel = finetuneSpinner.model as IntSpinnerModel
                debug("finetune changed : "+intModel.value)
                model.fineTuneCentOffset = intModel.value
                finetuneChangeListener?.invoke(intModel.value)
            }
        })

        val gainBtn = EnvelopeButton(null,"", VisUI.getSkin())
        val gainLinkLabel = LinkLabel("Gain:","")
        gainLinkLabel.setListener {
            url->
            println("clicked gain link label")
        }
        gainSlider = Slider(0f, 1f, .01f, false, skin)
        gainSlider.value = 1f
        gainSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                debug("gain changed : "+gainSlider.value)
                model.amplitude = gainSlider.value
                gainChangeListener?.invoke(gainSlider.value)
            }
        })

        unisonSpinner = Spinner("", IntSpinnerModel(1,0, 10, 1))
        unisonSpinner.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val intModel = unisonSpinner.model as IntSpinnerModel
                debug("unison changed : "+intModel.value)
                model.unisonVoices = intModel.value
                unisonChangeListener?.invoke(intModel.value)
            }
        })

        add(VisLabel("Form:")).padLeft(5f)
        add(waveformsSelectbox).fillX().expandX().colspan(5).padRight(5f)
        row()

        add(semitonesLinkLabel)
        add(semitonesBtn)
        add(semitonesSpinner)

        add(finetuneLinkLabel)
        add(finetuneBtn)
        add(finetuneSpinner)
        row()

        val cell = add(waveformPanel).expand().fill().left().colspan(6)

        row()

        add(VisLabel("Uni:"))
        add(unisonSpinner)
        add(gainLinkLabel)
        add(gainBtn)
        add(gainSlider).colspan(2)
        row()
        invalidate()
        invalidateHierarchy()
        layout()
    }

    private fun piecewiseModelDeleted(piecewise : PiecewiseModel){
        if(model.amplitudeEnv?.name == piecewise.name){
            model.amplitudeEnv = null
        }
        if(model.fineTuneCentOffsetEnv?.name == piecewise.name){
            model.fineTuneCentOffsetEnv = null
        }
        if(model.semitoneOffsetEnv?.name == piecewise.name){
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