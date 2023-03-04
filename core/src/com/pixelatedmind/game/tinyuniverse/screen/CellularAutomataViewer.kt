package com.pixelatedmind.game.tinyuniverse.screen

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import com.badlogic.gdx.utils.ScreenUtils
import com.pixelatedmind.game.tinyuniverse.generation.RectangleFactoryNormalDistributionImpl
import com.pixelatedmind.game.tinyuniverse.generation.RectangleFactorySeparationDecoratorImpl
import com.pixelatedmind.game.tinyuniverse.generation.VectorFactoryEllipseImpl
import com.pixelatedmind.game.tinyuniverse.generation.cellautomata.AutomataPatternGenerator3N
import com.pixelatedmind.game.tinyuniverse.generation.region.RegionGenerator
import com.pixelatedmind.game.tinyuniverse.generation.region.RegionModel
import com.pixelatedmind.game.tinyuniverse.input.KeyboardCameraMoveProcessor
import com.pixelatedmind.game.tinyuniverse.input.ScrollZoomInputProcessor
import com.pixelatedmind.game.tinyuniverse.maps.tiled.RegionModelTiledMapTileLayerMapper
import com.pixelatedmind.game.tinyuniverse.util.AutoTileExpanderUtil
import java.io.File
import java.util.*

class CellularAutomataViewer : ApplicationAdapter() {
    lateinit var camera : OrthographicCamera
    lateinit var pattern : MutableList<List<Boolean>>
    lateinit var shapeRenderer: ShapeRenderer

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

        generatePattern()
        super.create()
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
            val nextState = patternGen.generatePattern(state)
            pattern.add(nextState)
            i++
        }
    }

    override fun render() {
        ScreenUtils.clear(.2f, .2f, .2f, 1f)
        camera.update()
        shapeRenderer.projectionMatrix = camera.combined
        val size = -30f
        shapeRenderer.setColor(1f,0f,0f,1f)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        pattern.forEachIndexed{yIndex,row->
            val y = yIndex * size
            row.forEachIndexed { xIndex, value ->
                val x = xIndex * size
                if(value){
                    shapeRenderer.rect(x,y,size,size)
                }
            }
        }
        shapeRenderer.end()
    }
}