package com.pixelatedmind.game.tinyuniverse.screen

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import com.pixelatedmind.game.tinyuniverse.maps.tiled.IntAryBitmap
import com.pixelatedmind.game.tinyuniverse.maps.tiled.RegionTileCellMapper
import com.pixelatedmind.game.tinyuniverse.util.AutoTilePacker2
import java.io.File
import java.util.*


class AutoTileTester : ApplicationAdapter() {
    private lateinit var bitmap : IntAryBitmap
    private lateinit var tiledMap : TiledMap
    lateinit var camera : OrthographicCamera
    lateinit var tiledMapRenderer : OrthogonalTiledMapRenderer
    lateinit var tiles : Array<TiledMapTile>
    init{

    }

    fun createAutoTile(texture : Texture, startX:Int, startY:Int,width:Int, height:Int, tileWidth:Int, tileHeight:Int) : List<TiledMapTile> {
        AutoTilePacker2().load(
                File("waterGrassAutoTile.png"),32)

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

        val tiles = regions.map{StaticTiledMapTile(it)}
        return tiles
    }

    override fun create() {
        val rows = 10
        val columns = 10

        val ary = Array(rows){IntArray(columns){1} }


        bitmap = IntAryBitmap(ary, 1)
        val texture = Texture("test.png")
        val tiles = createAutoTile(texture,0,0,texture.width,texture.height,32,32)
        val mapper = RegionTileCellMapper(bitmap, tiles)
        var x = 0
        val tileLayer = TiledMapTileLayer(columns, rows, 32, 32)
        while(x<columns){
            var y = 0
            while(y<rows){
                if(x==2 && y == 0){
                    println("boop")
                }
                val cell = mapper.resolveCell(x, y)
                tileLayer.setCell(x, y, cell)
                ++y
            }
            ++x
        }
        tiledMap = TiledMap()
        val layers = tiledMap.layers
        layers.add(tileLayer)

        val w = Gdx.graphics.width.toFloat()
        val h = Gdx.graphics.height.toFloat()

        camera = OrthographicCamera()
        camera.setToOrtho(false, w, h)
        camera.update()
        tiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap)
        //Gdx.input.setInputProcessor(this)
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        camera.position.x = -50f
        camera.position.y = -50f
        camera.update()
        tiledMapRenderer.setView(camera)
        tiledMapRenderer.render()

    }
}