package com.pixelatedmind.game.tinyuniverse.screen

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.ScreenUtils
import com.pixelatedmind.game.tinyuniverse.generation.music.*
import com.pixelatedmind.game.tinyuniverse.generation.music.model.Chord
import com.pixelatedmind.game.tinyuniverse.generation.music.model.Note
import com.pixelatedmind.game.tinyuniverse.generation.music.model.Scale
import com.pixelatedmind.game.tinyuniverse.generation.music.proc.*
import com.pixelatedmind.game.tinyuniverse.generation.music.proc.patternfinder.ChordArpeggioBandRole
import com.pixelatedmind.game.tinyuniverse.generation.music.proc.patternfinder.ChordProgressionBandRole
import com.pixelatedmind.game.tinyuniverse.generation.music.proc.patternfinder.MovingBandRoleImpl
import com.pixelatedmind.game.tinyuniverse.generation.music.song.*
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.io.AudioStreamPlayer
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.io.AudioStreamReader
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.patch.*
import com.pixelatedmind.game.tinyuniverse.input.KeyboardCameraMoveProcessor
import com.pixelatedmind.game.tinyuniverse.input.ScrollZoomInputProcessor
import java.util.*

class CellularAutomataViewer : ApplicationAdapter() {
    lateinit var camera : OrthographicCamera
    lateinit var shapeRenderer: ShapeRenderer

    lateinit var musicPlayer : AudioStreamPlayer
    lateinit var generatedSong : SongResult

    val colors = listOf(
            color(65,88,252,200),
            color(44,209,94,200),
            color(170,0,0,200),
            color(64,0,128,200)
    )

    private fun color(r : Int, g : Int, b : Int, a: Int): Color{
        return Color(r/255f, g/255f,b/255f,a/255f)
    }

    override fun create() {

        val w = Gdx.graphics.width.toFloat()
        val h = Gdx.graphics.height.toFloat()

        shapeRenderer = ShapeRenderer()
        shapeRenderer.setAutoShapeType(true)

        camera = OrthographicCamera()
        camera.setToOrtho(false, w, h)

        val zoomInput = ScrollZoomInputProcessor(camera!!)
        val moveInput = KeyboardCameraMoveProcessor(camera!!)
        val multiplex = InputMultiplexer(zoomInput, moveInput)
        Gdx.input.setInputProcessor(multiplex)

        buildAndStartSong()
        super.create()
    }

    fun buildAndStartSong(){
        val samplingRate = 44100
        val playbackBufferSecs = 1/16f
        val audioDevice = Gdx.audio.newAudioDevice(samplingRate,true)
        musicPlayer = AudioStreamPlayer(audioDevice, samplingRate, playbackBufferSecs)

        this.generatedSong = buildSong()

        val songStream = SongStream(generatedSong.songModel, InstrumentRepositoryImpl(
                mapOf(
                        "kickdrum" to KickDrum(-.5f),
                        "electricPiano" to ElectricPiano(),
                        "reeseBass" to ReeseBass(),
                        "organ" to ChurchOrgan(1f),
                        "sine" to SinePatch()
                )
        )
        )
        musicPlayer.start(AudioStreamReader(songStream, 44100))
    }

    fun buildSong() : SongResult {
        val builder = SongBuilder()
        val width = 500

        val r1 = Random()
        //-6517403840780750149
        val seed = r1.nextLong()
        val random = Random(seed)
        println("seed = $seed")
        val initialState = MutableList(width){random.nextBoolean()}//it %4 == 0}

        val notes = Notes()
        val scale = Scale.Major
        val diatonicChords = notes.getDiatonicChords(scale)
        val cMajor = diatonicChords.filter{ it.rootNote == Note.C && it.chord == Chord.Major }.first()
        val gMajor = diatonicChords.filter{ it.rootNote == Note.G && it.chord == Chord.Major }.first()
        val aMinor = diatonicChords.filter{ it.rootNote == Note.A && it.chord == Chord.Minor }.first()
        val fMajor = diatonicChords.filter{ it.rootNote == Note.F && it.chord == Chord.Major }.first()

        val offset = 12
        val progression = mutableListOf<List<Int>>()
        progression.add(cMajor.semitonesFromScaleRoot.map{it+offset})
        progression.add(gMajor.semitonesFromScaleRoot.map{it+offset})
        progression.add(aMinor.semitonesFromScaleRoot.map{it+offset})
        progression.add(fMajor.semitonesFromScaleRoot.map{it+offset})

        builder.beatsPerMinute = 120
        builder.setBottomOctave(3)
        builder.setPattern(12*3,30, initialState)
        builder.rolePatternFinders.add(BandRoleModel("electricPiano",.5f,
                ChordProgressionBandRole(progression)))
        builder.rolePatternFinders.add(BandRoleModel("electricPiano",1f,
                ChordArpeggioBandRole(progression,10)
//                MovingBandRoleImpl(scale.notation.toList().map{it+12}, Random())
        ))
        builder.rolePatternFinders.add(BandRoleModel("sine",1f,
                MovingBandRoleImpl(scale.notation.toList().map{it}, Random())))
//        builder.rolePatternFinders.add(BandRoleModel("kickdrum", 1f,
//                MovingBandRoleImpl(scale.notation.toList().map{it}, Random())))
        val model = builder.newSong()
        return model
    }

    override fun render() {
        ScreenUtils.clear(1f, 1f, 1f, 1f)
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        camera.update()
        shapeRenderer.projectionMatrix = camera.combined
        renderPattern()
    }


    fun renderRolePattern(x:Float, y:Float, size : Float, cells : List<AutomataCell>){
        cells.filter{!it.isRest}.forEach{cell->
            val xIndex = x+cell.xIndex*size
            val yIndex = y+cell.yIndex*size
            shapeRenderer.rect(xIndex,yIndex,size*cell.width,size)
        }
    }

    fun renderPattern(){
        val size = 30f
        val pattern = generatedSong.fullPattern

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(.2f,.2f,.2f,.2f)
        var y = 0
        while(y<pattern.getHeight()){
            var x = 0
            val yIndex = y*size
            while(x<pattern.getWidth()){
                val xIndex = x*size
                val value = pattern.getValue(x,y)
                if(value){
                    shapeRenderer.rect(xIndex,yIndex,size,size)
                }
                x++
            }
            y++
        }
        generatedSong.voicePatterns.forEachIndexed{index,value->
            shapeRenderer.setColor(colors[index])
            renderRolePattern(0f,0f,size, value.second)
        }
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        //outline pattern area
        shapeRenderer.setColor(0f,0f,0f,1f)
        shapeRenderer.rect(0f,0f,pattern.getWidth().toFloat()*size,pattern.getHeight().toFloat()*size)
        shapeRenderer.end()
    }
}