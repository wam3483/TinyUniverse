package com.pixelatedmind.game.tinyuniverse.screen

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import com.badlogic.gdx.utils.ScreenUtils
import com.pixelatedmind.game.tinyuniverse.generation.*
import com.pixelatedmind.game.tinyuniverse.generation.region.RegionGenerator
import com.pixelatedmind.game.tinyuniverse.generation.region.RegionModel
import com.pixelatedmind.game.tinyuniverse.input.KeyboardCameraMoveProcessor
import com.pixelatedmind.game.tinyuniverse.input.ScrollZoomInputProcessor
import com.pixelatedmind.game.tinyuniverse.maps.tiled.RegionModelTiledMapTileLayerMapper
import com.pixelatedmind.game.tinyuniverse.util.AutoTileExpanderUtil
import java.io.File
import java.util.*

class TiledMapRegionViewer : ApplicationAdapter() {
    lateinit var camera : OrthographicCamera
    lateinit var mapRenderer: OrthogonalTiledMapRenderer

    override fun create() {
        AutoTileExpanderUtil().load(
                File("waterGrassAutoTile.png"),32)
        val w = Gdx.graphics.width.toFloat()
        val h = Gdx.graphics.height.toFloat()

        camera = OrthographicCamera()
        camera.setToOrtho(false, w, h)

        createTiledMap()

        val zoomInput = ScrollZoomInputProcessor(camera!!)
        val moveInput = KeyboardCameraMoveProcessor(camera!!)
        val multiplex = InputMultiplexer(zoomInput, moveInput)
        Gdx.input.setInputProcessor(multiplex)

        super.create()
    }

    fun createAutoTile(texture : Texture, startX:Int, startY:Int, width:Int, height:Int, tileWidth:Int, tileHeight:Int) : List<TiledMapTile> {
        val rows = height / tileHeight
        val columns = width / tileWidth
        val regions = mutableListOf<TextureRegion>()
        for(y in 0..rows){
            for(x in 0..columns){
                val region = TextureRegion(texture,
                        startX+x*tileWidth,
                        startY+y*tileHeight,
                        tileWidth,
                        tileHeight)
                regions.add(region)
            }
        }

        val tiles = regions.map{ StaticTiledMapTile(it) }
        return tiles
    }

    private fun generateRegion(): RegionModel {
        val random = Random()
        val positionFactory = VectorFactoryEllipseImpl(10f,10f, random)
        val rectFactory = RectangleFactoryNormalDistributionImpl(positionFactory, random,30f,10f,3f,3f)
        val rectSeparationDecorator = RectangleFactorySeparationDecoratorImpl(rectFactory)
        val generator = RegionGenerator(rectSeparationDecorator, 50, random)
        return generator.newMainRoomGraph()
    }

    private fun createTiledMap(){
        val regionModel = generateRegion()

        val texture = Texture("test.png")
        val tiles = createAutoTile(texture,0,0,texture.width,texture.height,32,32)

        val tileLayerMapper = RegionModelTiledMapTileLayerMapper()
        val tileLayer = tileLayerMapper.map(regionModel, tiles)

        val tiledMap = TiledMap()
        val layers = tiledMap.layers
        layers.add(tileLayer)

        mapRenderer = OrthogonalTiledMapRenderer(tiledMap)
    }

    override fun render() {
        ScreenUtils.clear(0f, 0f, 0f, 1f)
        camera.update()
        mapRenderer.setView(camera)
        mapRenderer.render()
        super.render()
    }
}