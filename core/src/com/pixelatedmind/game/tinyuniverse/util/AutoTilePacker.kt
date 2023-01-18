package com.pixelatedmind.game.tinyuniverse.util

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch

import com.badlogic.gdx.graphics.glutils.FrameBuffer

class AutoTilePacker  : ApplicationAdapter {
    private val path: String
    val tileSize = 32
    val halfSize = tileSize/2f
    val autoTileIndex = Vector2()

    lateinit var wholeTiles: Array<Array<TextureRegion>>
    lateinit var frameBuffer : FrameBuffer
    lateinit var texture : Texture
    lateinit var camera : OrthographicCamera

    lateinit var batch: SpriteBatch

    lateinit var unpackedAutotile : Array<TextureRegion?>

    constructor() {
        path = "waterGrassAutoTile.png"
        autoTileIndex.set(0f, 0f)
    }

    fun drawTexture(region:TextureRegion, x:Float,y:Float,sourceX:Float, sourceY:Float, width:Float,height:Float){
        batch.draw(region,x,y,sourceX,sourceY,width,height,1f,1f,0f)
    }

    override fun render() {
        super.render()
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.setProjectionMatrix(camera.combined)
        batch.begin()
        unpackedAutotile.filter{it!=null}.forEach{
            batch.draw(it!!,0f,200f)
        }
        batch.end()
    }
    override fun create(){
        camera = OrthographicCamera(Gdx.graphics.getWidth().toFloat(),Gdx.graphics.getHeight().toFloat());
        texture = Texture(path)
        val region = TextureRegion(texture, (autoTileIndex.x * tileSize).toInt(), (autoTileIndex.y * tileSize).toInt(), tileSize * 3, tileSize * 4)
        wholeTiles = region.split(tileSize, tileSize)
        batch = SpriteBatch()
        frameBuffer = FrameBuffer(Pixmap.Format.RGBA8888, tileSize, tileSize, false)
        unpackedAutotile = unpackAutoTiles()
    }

    fun tile1():TextureRegion{
        frameBuffer.begin();
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.setProjectionMatrix(camera.combined)
        batch.begin()
        batch.draw(texture,0f,0f)
//        drawTexture(wholeTiles[0][2],halfSize,halfSize,0f,halfSize,halfSize,halfSize)
//        drawTexture(wholeTiles[2][2],0f,halfSize,halfSize,halfSize,halfSize,halfSize)
//        drawTexture(wholeTiles[2][0])
        batch.end()
        frameBuffer.end();
        return TextureRegion(frameBuffer.colorBufferTexture)
    }

    fun unpackAutoTiles() : Array<TextureRegion?>{
        val unpackedTiles = arrayOfNulls<TextureRegion>(48)
        //unpackedTiles[0] = wholeTiles[1][0]
        unpackedTiles[0] = tile1()
        //unpackedTiles[45] = wholeTiles[1][0]
        return unpackedTiles
    }
}