package com.pixelatedmind.game.tinyuniverse.screen.synthui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.PassType
import java.text.DecimalFormat

class SynthUIBuilder {

    var volumeChangeListener : ((Float) -> Unit)? = null
    var subbassChangeListener : ((Float) -> Unit)? = null
    var oscillatorMixChangeListener : ((Float) -> Unit)? = null
    var oscillator2SemiToneOffsetChangeListener : ((Int) -> Unit)? = null
    var oscillatorChangeListener : ((Int, Oscillator) -> Unit)? = null
    var lowPassFilterChangeListener : ((Float,Float, Boolean, PassType) -> Unit)? = null
    var AHDSRChangeListener : ((Float,Float, Float, Float, Float) -> Unit)? = null


    val twoDecPlacesFormatter = DecimalFormat("#.##")

    private fun createAmpEnvelopeUI(maxValue : Float, skin : Skin,horizGroup : HorizontalGroup){
        val step = 10f
        val attackSlider = Slider(0f, maxValue, step,true, skin)
        val holdSlider = Slider(0f, maxValue, step,true, skin)
        val decaySlider = Slider(0f, maxValue, step,true, skin)
        val sustainSlider = Slider(0f, maxValue, step,true, skin)
        val releaseSlider = Slider(0f, maxValue, step,true, skin)

        val interpolationAry = arrayOf(
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
        val interpolationOptions = Array(interpolationAry)

        //it doesn't make a lot of sense to have hold and sustain interpolations as traditionally they maintain the attack
        //amplitude and end decay amplitude respectively; however I like the flexibility of thinking of hold and sustain as
        //just 2 more piece-wise functions that *may* be configured to behave in a traditional way.
        val attackInterpolation = SelectBox<String>(skin)
        val holdInterpolation = SelectBox<String>(skin)
        val decayInterpolation = SelectBox<String>(skin)
        val sustainInterpolation = SelectBox<String>(skin)
        val releaseInterpolation = SelectBox<String>(skin)

        attackInterpolation.setItems(interpolationOptions)
        holdInterpolation.setItems(interpolationOptions)
        decayInterpolation.setItems(interpolationOptions)
        sustainInterpolation.setItems(interpolationOptions)
        releaseInterpolation.setItems(interpolationOptions)

        val Ams = Label("0ms",skin)
        val Hms = Label("0ms",skin)
        val Dms = Label("0ms",skin)
        val Sms = Label("0ms",skin)
        val Rms = Label("0ms",skin)

        attackSlider.addListener( object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                Ams.setText("${attackSlider.value.toInt()}ms")
                AHDSRChangeListener?.invoke(
                        attackSlider.value,
                        holdSlider.value,
                        decaySlider.value,
                        sustainSlider.value,
                        releaseSlider.value)
            }
        })
        holdSlider.addListener( object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                Hms.setText("${holdSlider.value.toInt()}ms")
                AHDSRChangeListener?.invoke(
                        attackSlider.value,
                        holdSlider.value,
                        decaySlider.value,
                        sustainSlider.value,
                        releaseSlider.value)
            }
        })
        decaySlider.addListener( object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                Dms.setText("${decaySlider.value.toInt()}ms")
                AHDSRChangeListener?.invoke(
                        attackSlider.value,
                        holdSlider.value,
                        decaySlider.value,
                        sustainSlider.value,
                        releaseSlider.value)
            }
        })
        sustainSlider.addListener( object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                Sms.setText("${sustainSlider.value.toInt()}ms")
                AHDSRChangeListener?.invoke(
                        attackSlider.value,
                        holdSlider.value,
                        decaySlider.value,
                        sustainSlider.value,
                        releaseSlider.value)
            }
        })
        releaseSlider.addListener( object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                Rms.setText("${releaseSlider.value.toInt()}ms")
                AHDSRChangeListener?.invoke(
                        attackSlider.value,
                        holdSlider.value,
                        decaySlider.value,
                        sustainSlider.value,
                        releaseSlider.value)
            }
        })

        val asdrTable = Table()
        asdrTable.add(Ams).width(75f).center()
        asdrTable.add(Hms).width(75f).center()
        asdrTable.add(Dms).width(75f).center()
        asdrTable.add(Sms).width(75f).center()
        asdrTable.add(Rms).width(75f).center()
        asdrTable.row()

        asdrTable.add(attackSlider)
        asdrTable.add(holdSlider)
        asdrTable.add(decaySlider)
        asdrTable.add(sustainSlider)
        asdrTable.add(releaseSlider)
        asdrTable.row()
        asdrTable.add(Label("A",skin))
        asdrTable.add(Label("H",skin))
        asdrTable.add(Label("D",skin))
        asdrTable.add(Label("S",skin))
        asdrTable.add(Label("R",skin))
        asdrTable.row()

        horizGroup.addActor(asdrTable)//.colspan(3)
    }

    private fun buildVolumeSlider(skin : Skin, table : Table){
        val volumeLabel = Label("Volume", skin)
        val volumeSlider = Slider(-1f, 1f, .01f,false, skin)
        val volumeValue = Label("0", skin)
        volumeSlider.setValue(0f)
        volumeSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                volumeChangeListener?.invoke(volumeSlider.value)
                volumeValue.setText(twoDecPlacesFormatter.format(volumeSlider.value))
            }
        })
        table.add(volumeLabel).padLeft(10f).left()
        table.row()
        val table2 = Table()
        table2.add(volumeSlider).pad(10f)
        table2.add(volumeValue).pad(10f).width(50f)
        table.add(table2)
    }

    private fun buildOscillator2ToneOffsetUI(skin: Skin, table : Table){
        val o2SemiToneOffsetSlider = Slider(0f, 36f, 1f,false, skin)
        val o2SemiToneOffsetLabel = Label("0", skin)
        table.add(o2SemiToneOffsetSlider)
        table.add(o2SemiToneOffsetLabel)
        o2SemiToneOffsetSlider.addListener( object : ChangeListener(){
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val semiToneOffset = o2SemiToneOffsetSlider.value.toInt()
                if(semiToneOffset % 12 == 0){
                    val octaveOffset = semiToneOffset / 12
                    o2SemiToneOffsetLabel.setText("${semiToneOffset} (${octaveOffset} Oct)")
                }
                else if(semiToneOffset % 12 == 7) {//a fifth
                    val octaveOffset = semiToneOffset / 12
                    if(octaveOffset>0){
                        o2SemiToneOffsetLabel.setText("${semiToneOffset} (${octaveOffset} Oct & Fifth)")
                    }else{
                        o2SemiToneOffsetLabel.setText("${semiToneOffset} (Fifth)")
                    }
                }
                else {
                    o2SemiToneOffsetLabel.setText("${semiToneOffset}")
                }
                oscillator2SemiToneOffsetChangeListener?.invoke(semiToneOffset)
            }
        })
    }

    fun buildSubbassUI(skin:Skin, table:Table){
        val subbassSlider = Slider(-1f, 1f, .01f,false, skin)
        subbassSlider.setValue(0f)
        val subbassLabel = Label("0.0", skin)
        subbassSlider.addListener( object : ChangeListener(){
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                subbassLabel.setText(twoDecPlacesFormatter.format(subbassSlider.value))
                subbassChangeListener?.invoke(subbassSlider.value)
            }
        })
        table.add(Label("Sub bass",skin))
        table.row()
        table.add(subbassSlider)
        table.add(subbassLabel)
        table.row()
    }

    fun buildLowpassFilterUI(skin: Skin, table : Table){
        val lowHighDropDown = SelectBox<PassType>(skin)
        lowHighDropDown.setItems(PassType.Low, PassType.High)

        val lowHighPassEnableCheckbox = CheckBox("Low/High Pass Filter", skin)
        lowHighPassEnableCheckbox.isChecked = false

        val lowHighPassFilterSlider = Slider(0f, 1f, .01f,false, skin)
        lowHighPassFilterSlider.isDisabled = true

        val resonanceSlider = Slider(0f, 1f, .01f, false, skin)
        resonanceSlider.value = 0f
        resonanceSlider.isDisabled = true

        val lowHighPassFilterPercentLabel = Label("0%", skin)
        val lowHighPassFilterResonanceLabel = Label("0.0", skin)

        lowHighPassFilterSlider.addListener( object : ChangeListener(){
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val mixBalance = (lowHighPassFilterSlider.value*100).toInt()
                lowHighPassFilterPercentLabel.setText("${mixBalance}%")
                lowPassFilterChangeListener?.invoke(lowHighPassFilterSlider.value, resonanceSlider.value,
                        lowHighPassEnableCheckbox.isChecked, lowHighDropDown.selected)
            }
        })
        lowHighPassEnableCheckbox.addListener(object : ChangeListener(){
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val enabled = lowHighPassEnableCheckbox.isChecked
                lowHighPassFilterSlider.isDisabled = !enabled
                lowHighDropDown.isDisabled = !enabled
                resonanceSlider.isDisabled = !enabled
                lowPassFilterChangeListener?.invoke(lowHighPassFilterSlider.value, resonanceSlider.value,
                        lowHighPassEnableCheckbox.isChecked, lowHighDropDown.selected)
            }
        })
        lowHighDropDown.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                lowPassFilterChangeListener?.invoke(lowHighPassFilterSlider.value, resonanceSlider.value,
                        lowHighPassEnableCheckbox.isChecked, lowHighDropDown.selected)
            }
        })
        resonanceSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                lowHighPassFilterResonanceLabel.setText(twoDecPlacesFormatter.format(resonanceSlider.value))
                lowPassFilterChangeListener?.invoke(lowHighPassFilterSlider.value, resonanceSlider.value,
                        lowHighPassEnableCheckbox.isChecked, lowHighDropDown.selected)
            }
        })

        table.add(lowHighPassEnableCheckbox).padTop(20f)
        table.row()
        table.add(lowHighDropDown)
        table.row()
        table.add(Label("Cutoff",skin))
        table.add(lowHighPassFilterSlider)
        table.add(lowHighPassFilterPercentLabel)
        table.row()
        table.add(Label("Resonance",skin))
        table.add(resonanceSlider)
        table.add(lowHighPassFilterResonanceLabel)
    }

    fun buildUI() : Stage{
        val stage = Stage()
        val horizStack = HorizontalGroup()

        val table = Table()
//        table.setFillParent(true)

        horizStack.addActor(table)
        horizStack.setFillParent(true)

        stage.addActor(horizStack)
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
        createAmpEnvelopeUI(10000f, skin, horizStack)
        table.row()
        table.add(o2Label)
        table.add(oscillatorDropdown2)
        buildOscillator2ToneOffsetUI(skin, table)

        table.row()
        table.add()
        table.add(Label("Mix", skin)).bottom().center()
        table.row()
        table.add(o1MixLabel).width(100f)
        table.add(mixSlider)
        table.add(o2MixLabel).width(100f)
        table.row()

        buildLowpassFilterUI(skin, table)

        table.row()

        buildVolumeSlider(skin, table)
        table.row()

        buildSubbassUI(skin, table)
        table.left().top()

        return stage
    }
}