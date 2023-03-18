package com.pixelatedmind.game.tinyuniverse.screen

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.audio.AudioDevice
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.ScreenUtils
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.PassType
import com.pixelatedmind.game.tinyuniverse.generation.music.proc.BandRoleModel
import com.pixelatedmind.game.tinyuniverse.generation.music.proc.patternfinder.MovingBandRoleImpl
import com.pixelatedmind.game.tinyuniverse.generation.music.proc.SongBuilder
import com.pixelatedmind.game.tinyuniverse.generation.music.proc.SongResult
import com.pixelatedmind.game.tinyuniverse.generation.music.song.InstrumentRepositoryImpl
import com.pixelatedmind.game.tinyuniverse.generation.music.model.Scale
import com.pixelatedmind.game.tinyuniverse.generation.music.song.SongStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.AdditiveNoteGenerator
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.AudioDeviceFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.io.AudioStreamPlayer
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.io.AudioStreamReader
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.patch.*
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.input.KeyboardCameraMoveProcessor
import com.pixelatedmind.game.tinyuniverse.input.NoteGeneratorKeyboardProcessor
import com.pixelatedmind.game.tinyuniverse.input.ScrollZoomInputProcessor
import com.pixelatedmind.game.tinyuniverse.screen.synthui.Oscillator
import com.pixelatedmind.game.tinyuniverse.screen.synthui.SynthUIBuilder
import space.earlygrey.shapedrawer.ShapeDrawer
import java.util.*

class AudioOutputViewer(val deviceFactory : AudioDeviceFactory) : ApplicationAdapter() {
    lateinit var synthUI : SynthUIBuilder
    lateinit var stage : Stage
    lateinit var patch : InteractivePatch

    lateinit var audioDevice : AudioDevice
    lateinit var musicPlayer : AudioStreamPlayer

    lateinit var camera : OrthographicCamera

    lateinit var batch : SpriteBatch
    lateinit var drawer : ShapeDrawer

    var soundOutput : FloatArray? = null

    val waveformWidth = 10000f
    val waveformHeight = 400f

    fun initShapeDrawer(){
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        val color = 0xFFFFFFFF
        pixmap.setColor(color.toInt())
        pixmap.drawPixel(0, 0)
        val texture = Texture(pixmap) //remember to dispose of later

        pixmap.dispose()
        val region = TextureRegion(texture, 0, 0, 1, 1)
        drawer = ShapeDrawer(batch, region)
    }

    fun initUI(){
        synthUI = SynthUIBuilder()
        stage = synthUI.buildUI()
        synthUI.oscillatorMixChangeListener = {
            mixNormValue:Float ->
            patch.oscillatorMix = mixNormValue
        }
        synthUI.lowPassFilterChangeListener = {cutoffFreq:Float, resonance:Float, enabled:Boolean, passType: PassType ->
            patch.highlowFilterEnabled = enabled
            patch.highlowFilter.setPassType(passType)
            patch.highlowFilterCutoffFreq = cutoffFreq
            patch.highlowFilterResonance = resonance
        }
        synthUI.oscillatorChangeListener = {oscillatorIndex:Int, oscillator:Oscillator->
            if(oscillatorIndex == 0){
                patch.oscillator1 = oscillator
            }
            else{
                patch.oscillator2 = oscillator
            }
        }
        synthUI.oscillator2SemiToneOffsetChangeListener = {semitoneOffset : Int->
            patch.oscillator2SemitoneOffset = semitoneOffset
        }
        synthUI.volumeChangeListener = {volumeDb:Float ->
            patch.volumeOffsetInDecibels = volumeDb
        }
        synthUI.subbassChangeListener = {subBass: Float->
            patch.subBass = subBass
        }
        synthUI.AHDSRChangeListener = {attack:Float, hold:Float,decay:Float,sustain:Float, release:Float->
            patch.attack = attack
            patch.hold = hold
            patch.decay = decay
            patch.sustain = sustain
            patch.release = release
        }
    }

    fun buildSong() : SongResult {
        val builder = SongBuilder()
        val width = 50
        val initialState = MutableList(width){false}
        initialState[width /2] = true
        initialState[width/2+5] = true

        builder.setBottomOctave(4)
        builder.setPattern(12,30,initialState)
        builder.rolePatternFinders.add(BandRoleModel("electricPiano",1f, MovingBandRoleImpl(
                Scale.Major.notation.toList(),Random())))
        val model = builder.newSong()
        return model
    }

    override fun create() {
        val generatedSong = buildSong()
//        val songModel = PercussionBuilder().newSong(
//            mapOf(
//                "kickdrum" to EuclideanRhythmGenerator().getBeats(13,23, 1),
//                "organ" to EuclideanRhythmGenerator().getBeats(7,23, 1)
//            )
//        )
        val songStream = SongStream(generatedSong.songModel, InstrumentRepositoryImpl(
                mapOf(
                    "kickdrum" to KickDrum(-.5f),
                    "electricPiano" to ElectricPiano()
                )
            )
        )

        patch = InteractivePatch()
        initUI()

        val w = Gdx.graphics.width.toFloat()
        val h = Gdx.graphics.height.toFloat()

        camera = OrthographicCamera()
        camera.setToOrtho(false, w, h)

        val zoomInput = ScrollZoomInputProcessor(camera!!)
        val moveInput = KeyboardCameraMoveProcessor(camera!!)

        patch.oscillator1 = Oscillator.Sine

        batch = SpriteBatch()
        initShapeDrawer()

        val audioDevice = deviceFactory.newDevice(44100, true)
        //val audio =Gdx.audio
        //audioDevice = audio.newAudioDevice(44100,true)
        musicPlayer = AudioStreamPlayer(audioDevice, 44100, 1/16f)
        musicPlayer.addPlaybackListener { ary->
            soundOutput = ary
        }
        val bounce = BasketballBounce(-.4f)
        val churchOrganPatch = ChurchOrgan(.4f)
        val kickdrum = KickDrum(1f)
        val electricPiano = ElectricPiano()
        val clapPatch = ClapPatch(44100f)
        val pluckBass = PluckBass()
//        val synthWars = SongStream.synthWars(churchOrganPatch, 5)
        val additiveNoteGenerator = AdditiveNoteGenerator(pluckBass)

        var stream : FloatInputStream = additiveNoteGenerator
        stream = songStream
        musicPlayer.start(AudioStreamReader(additiveNoteGenerator, 44100))

        val keyboard = NoteGeneratorKeyboardProcessor(additiveNoteGenerator)

        val multiplex = InputMultiplexer(zoomInput, keyboard, moveInput, stage)
        Gdx.input.inputProcessor = multiplex
    }

    override fun render(){
        ScreenUtils.clear(.4f, .4f, .4f, 1f)
        camera.update()
        batch.projectionMatrix = camera!!.combined

        drawer.batch.begin()

        val audioBuffer = soundOutput
        if(audioBuffer!=null) {
            val xMult = waveformWidth / audioBuffer.size
            var last : Vector2? = null
            var current = Vector2()
            drawer.setColor(Color.BLACK)
            drawer.line(xMult, 0f, xMult*audioBuffer.size, 0f, 5f)
            drawer.setColor(Color.RED)
            audioBuffer.forEachIndexed { index, value ->
                val x = xMult * index
                val y = value * waveformHeight
                current.set(x,y)
                if(last!=null) {
                    drawer.line(last, current, 5f)
                }else{
                    last = Vector2()
                }
                last!!.set(current)
            }
        }
        drawer.batch.end()

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }
}