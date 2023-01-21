package com.pixelatedmind.game.tinyuniverse.screen

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.pixelatedmind.game.tinyuniverse.maps.tiled.IntAryBitmap
import com.pixelatedmind.game.tinyuniverse.maps.tiled.RegionTileCellMapper
import java.util.*


class AutoTileTester : ApplicationAdapter() {
    private val bitmap : IntAryBitmap
    private val tiledMap : TiledMap
    lateinit var camera : OrthographicCamera
    lateinit var tiledMapRenderer : OrthogonalTiledMapRenderer
    init{
        val rows = 10
        val columns = 10

        val ary = Array(rows){IntArray(columns)}
        val random = Random()
        repeat(50){
            val x = random.nextInt(columns)
            val y = random.nextInt(rows)
            ary[x][y] = 1
        }

        bitmap = IntAryBitmap(ary, 1)
        //TODO need to load and init an array of tiledmaptile
        val mapper = RegionTileCellMapper(bitmap, listOf<TiledMapTile>())
        var x = 0
        var y = 0
        val tileLayer = TiledMapTileLayer(columns, rows, 32, 32)
        while(x<columns){
            while(y<rows){
                val cell = mapper.resolveCell(x, y)
                tileLayer.setCell(x, y, cell)
                ++y
            }
            ++x
        }
        tiledMap = TiledMap()
        val layers = tiledMap.layers
        layers.add(tileLayer)
    }

    fun getAutoTiles() : List<TiledMapTile>{

    }

    override fun create() {
        val w = Gdx.graphics.width.toFloat()
        val h = Gdx.graphics.height.toFloat()

        camera = OrthographicCamera()
        camera.setToOrtho(false, w, h)
        camera.update()
        tiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap)
        //Gdx.input.setInputProcessor(this)
    }

    override fun render() {
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        camera.update()
        tiledMapRenderer.setView(camera)
        tiledMapRenderer.render()
    }
}