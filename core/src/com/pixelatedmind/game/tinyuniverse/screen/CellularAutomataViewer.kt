package com.pixelatedmind.game.tinyuniverse.screen

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.ScreenUtils
import com.pixelatedmind.game.tinyuniverse.generation.cellautomata.AutomataPatternGenerator3N
import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStreamReader
import com.pixelatedmind.game.tinyuniverse.generation.music.MusicManager
import com.pixelatedmind.game.tinyuniverse.generation.music.Scale
import com.pixelatedmind.game.tinyuniverse.generation.music.patch.KickDrum
import com.pixelatedmind.game.tinyuniverse.generation.music.patch.ElectricPiano
import com.pixelatedmind.game.tinyuniverse.generation.music.patch.ReeseBass
import com.pixelatedmind.game.tinyuniverse.generation.music.proc.AutomataCell
import com.pixelatedmind.game.tinyuniverse.generation.music.proc.BassMovingBandRoleImpl
import com.pixelatedmind.game.tinyuniverse.generation.music.proc.SongBuilder
import com.pixelatedmind.game.tinyuniverse.generation.music.proc.SongResult
import com.pixelatedmind.game.tinyuniverse.generation.music.song.InstrumentRepositoryImpl
import com.pixelatedmind.game.tinyuniverse.generation.music.song.SongStream
import com.pixelatedmind.game.tinyuniverse.generation.music.song.model.NoteTone
import com.pixelatedmind.game.tinyuniverse.input.KeyboardCameraMoveProcessor
import com.pixelatedmind.game.tinyuniverse.input.ScrollZoomInputProcessor
import java.util.*

class CellularAutomataViewer : ApplicationAdapter() {
    lateinit var camera : OrthographicCamera
    lateinit var pattern : MutableList<List<Boolean>>
    lateinit var shapeRenderer: ShapeRenderer

    lateinit var musicPlayer : MusicManager
    lateinit var generatedSong : SongResult

    val colors = listOf(
            Color.BLUE,
            Color.GREEN,
            Color.MAGENTA,
            Color.YELLOW
    )

    override fun create() {
        val w = Gdx.graphics.width.toFloat()
        val h = Gdx.graphics.height.toFloat()

        shapeRenderer = ShapeRenderer()
        shapeRenderer.setAutoShapeType(true)

        camera = OrthographicCamera()
        camera.setToOrtho(true, w, h)

        val zoomInput = ScrollZoomInputProcessor(camera!!)
        val moveInput = KeyboardCameraMoveProcessor(camera!!)
        val multiplex = InputMultiplexer(zoomInput, moveInput)
        Gdx.input.setInputProcessor(multiplex)

        generatePattern()
        buildAndStartSong()
        super.create()
    }

    fun buildAndStartSong(){
        val samplingRate = 44100
        val playbackBufferSecs = 1/16f
        val audioDevice = Gdx.audio.newAudioDevice(samplingRate,true)
        musicPlayer = MusicManager(audioDevice, samplingRate, playbackBufferSecs)

        this.generatedSong = buildSong()

        val songStream = SongStream(generatedSong.songModel, InstrumentRepositoryImpl(
                mapOf(
                        "kickdrum" to KickDrum(-.5f),
                        "electricPiano" to ElectricPiano(),
                        "reeseBass" to ReeseBass()
                )
        )
        )
        musicPlayer.start(FloatInputStreamReader(songStream, 44100))
    }

    fun buildSong() : SongResult {
        val builder = SongBuilder()
        val width = 50

        val random = Random()
        val initialState = MutableList(width){it %4 == 0}
//        initialState[width /2] = true
//        initialState[width/2+5] = true

        builder.setScale(2, NoteTone.G, Scale.Major)
        builder.setPattern(12,30,initialState)
        builder.rolePatternFinders["reeseBass"] = BassMovingBandRoleImpl(Random(), .6f, 1f)
        builder.rolePatternFinders["electricPiano"] = BassMovingBandRoleImpl(Random(), 0f, .5f)
        val model = builder.newSong()
        return model
    }

    fun generatePattern(){
        val width = 43
        val height = 50
        pattern = mutableListOf()
        val initialState = MutableList(width){false}
        initialState[width/2] = true
        pattern.add(initialState)
        val patternGen = AutomataPatternGenerator3N(0b00011110)
        var i = 0
        while(i<height){
            val state = pattern[i]
            val nextState = patternGen.generatePattern1D(state)
            pattern.add(nextState)
            i++
        }
    }

    override fun render() {
        ScreenUtils.clear(.2f, .2f, .2f, 1f)
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
        shapeRenderer.setColor(1f,0f,0f,1f)
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