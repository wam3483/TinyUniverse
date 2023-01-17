package com.pixelatedmind.game.tinyuniverse.util

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.graphics.Pixmap

import com.badlogic.gdx.graphics.glutils.FrameBuffer




class AutoTilePacker  : ApplicationAdapter {
    private val path: String
    val tileSize = 32
    val autoTileIndex = Vector2()

    val wholeTiles: Array<Array<TextureRegion>>
    val frameBuffer : FrameBuffer

    constructor() {
        path = ""
        autoTileIndex.set(0f, 0f)
        val texture = Texture(path)
        val region = TextureRegion(texture, (autoTileIndex.x * tileSize).toInt(), (autoTileIndex.y * tileSize).toInt(), tileSize * 3, tileSize * 4)
        wholeTiles = region.split(tileSize, tileSize)
        frameBuffer = FrameBuffer(Pixmap.Format.RGBA8888, tileSize, tileSize, false)
        val bufferRegion = frameBuffer.colorBufferTexture
    }

    fun unpackAutoTiles() {
        val unpackedTiles = arrayOfNulls<TextureRegion>(48)
        unpackedTiles[0] = wholeTiles[1][0]

        unpackedTiles[45] = wholeTiles[1][0]
    }
}