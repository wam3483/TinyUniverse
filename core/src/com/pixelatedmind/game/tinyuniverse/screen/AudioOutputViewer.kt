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
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils
import com.pixelatedmind.game.tinyuniverse.generation.music.*
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.HighLowPassFilter
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.PassType
import com.pixelatedmind.game.tinyuniverse.generation.music.patch.TestPatch
import com.pixelatedmind.game.tinyuniverse.generation.music.waveform.*
import com.pixelatedmind.game.tinyuniverse.input.KeyboardCameraMoveProcessor
import com.pixelatedmind.game.tinyuniverse.input.NoteGeneratorKeyboardProcessor
import com.pixelatedmind.game.tinyuniverse.input.ScrollZoomInputProcessor
import space.earlygrey.shapedrawer.ShapeDrawer

class AudioOutputViewer() : ApplicationAdapter() {

    lateinit var audioDevice : AudioDevice
    lateinit var musicPlayer : MusicManager
    lateinit var floatInputStreamReader: FloatInputStreamReader

    lateinit var interpolatedWaveform : WaveformInterpolator
    lateinit var sineWavePitched : AbstractShapeWaveform

    lateinit var camera : OrthographicCamera

    lateinit var batch : SpriteBatch
    lateinit var drawer : ShapeDrawer

    lateinit var additiveNoteGenerator : AdditiveNoteGeneratorImpl

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

    val interpolationChangePerSecond = .1f
    override fun render(){
        val delta = Gdx.graphics.deltaTime * interpolationChangePerSecond
        interpolatedWaveform.alpha += delta
//        sineWavePitched.setPhaseShift(sineWavePitched.phaseShift +delta)
        if(interpolatedWaveform.alpha >1){
            interpolatedWaveform.alpha = 0f
        }
//        if(sineWavePitched.phaseShift>1){
//            sineWavePitched.phaseShift = 0f
//        }
//        sineWavePitched.frequency+=.1f
//        println(sineWavePitched.phaseShift)

        ScreenUtils.clear(.4f, .4f, .4f, 1f)
        camera.update()
        batch.projectionMatrix = camera!!.combined

        drawer.batch.begin()
        drawer.setColor(Color.RED)
        val audioBuffer = soundOutput
        if(audioBuffer!=null) {
            val xMult = waveformWidth / audioBuffer.size
            var last : Vector2? = null
            var current = Vector2()
            audioBuffer.forEachIndexed { index, value ->
                val x = xMult * index
                val y = value * waveformHeight
                current.set(x,y)
                if(last!=null) {
                    drawer.line(last, current)
                }else{
                    last = Vector2()
                }
                last!!.set(current)
            }
        }
        drawer.batch.end()
    }

    fun newPatch() : FloatInputStream{
        return SawtoothWaveform(1f)
    }

    override fun create() {
        val w = Gdx.graphics.width.toFloat()
        val h = Gdx.graphics.height.toFloat()

        camera = OrthographicCamera()
        camera.setToOrtho(false, w, h)

        val zoomInput = ScrollZoomInputProcessor(camera!!)
        val moveInput = KeyboardCameraMoveProcessor(camera!!)

        val patch = TestPatch()
        additiveNoteGenerator = AdditiveNoteGeneratorImpl(patch)
        val keyboard = NoteGeneratorKeyboardProcessor(additiveNoteGenerator)

        val multiplex = InputMultiplexer(zoomInput, moveInput, keyboard)
        Gdx.input.inputProcessor = multiplex

        batch = SpriteBatch()
        initShapeDrawer()

        initInputStream()

        val audio = Gdx.audio
        audioDevice = audio.newAudioDevice(44100,false)
        musicPlayer = MusicManager(audioDevice, 44100, .2f)
        musicPlayer.addPlaybackListener { ary->
            soundOutput = ary
        }
        musicPlayer.start(FloatInputStreamReader(additiveNoteGenerator, 44100))

        //musicPlayer.start(floatInputStreamReader)
    }

    private fun initInputStream(){
        val sineWave = SineWaveform(150f,1f)
        sineWavePitched = SineWaveform(152f,1f, .0f)
        val triangleWave = TriangleWaveForm(1f, 100f)
        val niceSoundingInterpolation = WaveformInterpolator(sineWave, triangleWave, .9f, Interpolation.linear)

        val sawtoothWaveform = SawtoothWaveform(1f, 261f)
        val squareWaveform = SquareWaveform(.5f, 100f)
        interpolatedWaveform = WaveformInterpolator(sineWave, triangleWave, 1f, Interpolation.linear)
        val additiveWaveform = AdditiveWaveform(listOf(
                sineWave,
                PhaseShiftWaveformDecorator(sineWavePitched, .53f)))
        val lowpassFilter = HighLowPassFilterInputStream(HighLowPassFilter(sawtoothWaveform.frequency*.5f, 44100, PassType.Low), sawtoothWaveform)
        floatInputStreamReader = FloatInputStreamReader(sawtoothWaveform, 44100)
    }
}