package com.pixelatedmind.game.tinyuniverse.util

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.graphics.g2d.SpriteBatch

import com.badlogic.gdx.graphics.glutils.FrameBuffer
import java.io.File

class AutoTilePacker  : ApplicationAdapter {
    private val path: String
    val tileSize = 32
    val halfSize = tileSize/2f
    val autoTileIndex = Vector2()

    lateinit var wholeTiles: Array<Array<TextureRegion>>
    lateinit var frameBuffer : FrameBuffer
    lateinit var texture : Texture
    lateinit var camera : OrthographicCamera
    lateinit var origin : Vector2

    lateinit var batch: SpriteBatch

    lateinit var unpackedAutotile : Array<TextureRegion?>

    constructor() {
        path = "waterGrassAutoTile.png"
        autoTileIndex.set(0f, 0f)
        AutoTilePacker2().load(File("C:\\Users\\wam34\\IdeaProjects\\TinyUniverse\\assets\\waterGrassAutoTile.png"),32)
    }

    fun drawTexture(region:TextureRegion, x:Float,y:Float,sourceX:Float, sourceY:Float, width:Float,height:Float){
        batch.draw(region,x,y,sourceX,sourceY,width,height,1f,1f,0f)
    }

    var hasSaved = false
    override fun render() {
        super.render()
        origin = Vector2(-Gdx.graphics.getWidth()/2f,Gdx.graphics.getHeight()/2f-tileSize)
//        origin.set(-Gdx.graphics.getWidth()/2f,0f)
        val screenWidth = Gdx.graphics.getWidth().toInt()
        val screenHeight = Gdx.graphics.getHeight().toInt()
        camera.update()
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.setProjectionMatrix(camera.combined)
        frameBuffer = FrameBuffer(Pixmap.Format.RGBA8888, screenWidth, screenHeight, false)
        val texture = unpackAutoTiles()
        batch.begin()
        batch.draw(texture,-screenWidth/2f,-screenHeight/2f)
        batch.end()
        frameBuffer.dispose()
    }

    fun extractPixmapFromTextureRegion(textureRegion:TextureRegion):Pixmap {
        val textureData = textureRegion.getTexture().getTextureData()
        if (!textureData.isPrepared()) {
            textureData.prepare();
        }
        val pixmap = Pixmap(
                textureRegion.getRegionWidth(),
                textureRegion.getRegionHeight(),
                textureData.getFormat()
        );
        pixmap.drawPixmap(
                textureData.consumePixmap(), // The other Pixmap
                0, // The target x-coordinate (top left corner)
                0, // The target y-coordinate (top left corner)
                textureRegion.getRegionX(), // The source x-coordinate (top left corner)
                textureRegion.getRegionY(), // The source y-coordinate (top left corner)
                textureRegion.getRegionWidth(), // The width of the area from the other Pixmap in pixels
                textureRegion.getRegionHeight() // The height of the area from the other Pixmap in pixels
        );
        return pixmap;
    }

    override fun create(){
        camera = OrthographicCamera(Gdx.graphics.getWidth().toFloat(),Gdx.graphics.getHeight().toFloat());
        texture = Texture(path)
        val region = TextureRegion(texture, (autoTileIndex.x * tileSize).toInt(), (autoTileIndex.y * tileSize).toInt(), tileSize * 3, tileSize * 4)
        wholeTiles = region.split(tileSize, tileSize)
        batch = SpriteBatch()
    }

    fun tile0(){
        batch.draw(wholeTiles[0][1],origin.x+0f,origin.y+0f)
    }

    fun tile1(){
        batch.draw(wholeTiles[2][2].split(halfSize.toInt(),halfSize.toInt())[1][1],origin.x+0f,origin.y+halfSize)
        batch.draw(wholeTiles[2][0].split(halfSize.toInt(),halfSize.toInt())[1][0],origin.x+halfSize,origin.y+halfSize)
        batch.draw(wholeTiles[0][2].split(halfSize.toInt(),halfSize.toInt())[0][0],origin.x+halfSize,origin.y+0f)
        batch.draw(wholeTiles[0][2].split(halfSize.toInt(),halfSize.toInt())[0][1],origin.x+0f,origin.y+0f)
    }

    fun tile2(){
        batch.draw(wholeTiles[1][1].split(halfSize.toInt(),halfSize.toInt())[0][1],origin.x+0f, origin.y)
        batch.draw(wholeTiles[3][1].split(halfSize.toInt(),halfSize.toInt())[1][1],origin.x+0f, origin.y+halfSize)

        batch.draw(wholeTiles[0][2].split(halfSize.toInt(),halfSize.toInt())[1][0],origin.x+halfSize, origin.y+halfSize)
        batch.draw(wholeTiles[0][2].split(halfSize.toInt(),halfSize.toInt())[0][0],origin.x+halfSize, origin.y)
    }

    fun tile3(){
        batch.draw(wholeTiles[0][0].split(halfSize.toInt(),halfSize.toInt())[1][1],0f+origin.x,halfSize+origin.y)
        batch.draw(wholeTiles[0][2].split(halfSize.toInt(),halfSize.toInt())[0][0],halfSize+origin.x,0f+origin.y)
        batch.draw(wholeTiles[1][1].split(halfSize.toInt(),halfSize.toInt())[0][1], 0f+origin.x, origin.y)
        batch.draw(wholeTiles[2][0].split(halfSize.toInt(),halfSize.toInt())[0][0], halfSize+origin.x, halfSize+origin.y)
    }

    fun tile4(){
        batch.draw(subTile(1,0,1,1),0f+origin.x,halfSize+origin.y)
        batch.draw(wholeTiles[0][2].split(halfSize.toInt(),halfSize.toInt())[0][0],halfSize+origin.x,0f+origin.y)
        batch.draw(wholeTiles[1][1].split(halfSize.toInt(),halfSize.toInt())[0][1], 0f+origin.x, 0f+origin.y)
        batch.draw(wholeTiles[2][0].split(halfSize.toInt(),halfSize.toInt())[0][0], halfSize+origin.x, halfSize+origin.y)
    }

    fun tile5(){
        batch.draw(subTile(2,0,1,1),0f+origin.x,halfSize+origin.y)
        batch.draw(subTile(1,3,0,1),halfSize+origin.x,halfSize+origin.y)
        batch.draw(subTile(2,0,1,0),0f+origin.x,0f+origin.y)
        batch.draw(subTile(1,1,0,0),halfSize+origin.x,0f+origin.y)
    }
    fun tile6(){
        batch.draw(subTile(2,2,1,1),0f+origin.x,halfSize+origin.y)
        batch.draw(subTile(0,0,0,1),halfSize+origin.x,halfSize+origin.y)
        batch.draw(subTile(2,0,1,0),0f+origin.x,0f+origin.y)
        batch.draw(subTile(1,1,0,0),halfSize+origin.x,0f+origin.y)
    }

    fun tile7(){
        draw(2,2,1,0,0,0)
        draw(1,0,1,0,1,0)
        draw(2,0,1,0,0,1)
        draw(1,1,1,0,1,1)
    }

    fun tile8(){
        draw(1,3,0,1,0,0)
        draw(1,3,1,1,1,0)
        draw(1,1,0,0,0,1)
        draw(1,1,1,0,1,1)
    }

    fun tile9(){
        batch.draw(subTile(0,0,1,1),0f+origin.x, halfSize+origin.y)
        batch.draw(subTile(0,0,0,1),halfSize+origin.x, halfSize+origin.y)
        batch.draw(subTile(1,1,0,0),0f+origin.x,0f+origin.y)
        batch.draw(subTile(1,1,1,0),halfSize+origin.x,0f+origin.y)
    }

    fun tile10(){
        batch.draw(subTile(1,0,1,1),0f+origin.x, halfSize+origin.y)
        batch.draw(subTile(0,0,0,1),halfSize+origin.x, halfSize+origin.y)
        batch.draw(subTile(1,1,0,0),0f+origin.x,0f+origin.y)
        batch.draw(subTile(1,1,1,0),halfSize+origin.x,0f+origin.y)
    }

    fun tile11(){
        batch.draw(subTile(0,0,1,1),0f+origin.x, halfSize+origin.y)
        batch.draw(subTile(1,0,0,0),halfSize+origin.x, halfSize+origin.y)
        batch.draw(subTile(1,1,0,0),0f+origin.x,0f+origin.y)
        batch.draw(subTile(1,1,1,0),halfSize+origin.x,0f+origin.y)
    }

    fun tile12(){
        draw(1,0,0,0,0,0)
        draw(1,0,1,0,1,0)
        draw(1,1,0,0,0,1)
        draw(1,1,1,0,1,1)
    }
    fun tile13(){
        draw(2,0,1,1,0,0)
        draw(2,0,0,1,1,0)
        draw(2,2,1,1,0,1)
        draw(0,2,0,1,1,1)
    }

    fun tile14(){
        draw(0,2,0,0,1,0)
        draw(0,2,0,1,1,1)
        draw(2,2,1,0,0,0)
        draw(2,2,1,1,0,1)
    }
    fun tile15(){
        draw(1,3,0,1,0,0)
        draw(2,0,0,1,1,0)
        draw(0,0,1,0,0,1)
        draw(0,2,0,1,1,1)
    }
    fun tile16(){
        draw(0,0,1,1,0,0)
        draw(0,2,0,0,1,0)
        draw(0,0,1,0,0,1)
        draw(0,2,0,1,1,1)
    }
    fun tile17(){
        draw(1,0,0,0,0,0)
        draw(0,2,0,0,1,0)
        draw(0,0,1,0,0,1)
        draw(0,2,0,1,1,1)
    }
    fun tile18(){
        draw(2,0,1,1,0,0)
        draw(1,3,1,1,1,0)
        draw(2,2,1,1,0,1)
        draw(0,0,0,0,1,1)
    }
    fun tile19(){
        draw(2,2,1,0,0,0)
        draw(0,0,0,1,1,0)
        draw(2,2,1,1,0,1)
        draw(0,0,0,0,1,1)
    }
    fun tile20(){
        draw(2,2,1,0,0,0)
        draw(1,0,1,0,1,0)
        draw(2,2,1,1,0,1)
        draw(0,0,0,0,1,1)
    }

    fun tile21(){
        draw(1,3,0,1,0,0)
        draw(1,3,1,1,1,0)
        draw(0,0,1,0,0,1)
        draw(0,0,0,0,1,1)
    }

    fun tile22(){
        draw(0,0,1,1,0,0)
        draw(0,0,0,1,1,0)
        draw(0,0,1,0,0,1)
        draw(0,0,0,0,1,1)
    }

    fun tile23(){
        draw(1,0,0,0,0,0)
        draw(0,0,0,1,1,0)
        draw(0,0,1,0,0,1)
        draw(1,0,1,1,1,1)
    }

    fun tile24(){
        draw(0,0,1,1,0,0)
        draw(1,0,1,0,1,0)
        draw(0,0,1,0,0,1)
        draw(0,0,0,0,1,1)
    }

    fun tile25(){
        draw(1,0,0,0,0,0)
        draw(1,0,1,0,1,0)
        draw(0,0,1,0,0,1)
        draw(0,0,0,0,1,1)
    }

    fun tile26(){
        draw(0,0,1,1,0,0)
        draw(1,0,1,0,1,0)
        draw(1,0,0,1,0,1)
        draw(0,0,0,0,1,1)
    }
    fun tile27(){
        draw(1,0,0,0,0,0)
        draw(0,0,0,1,1,0)
        draw(1,0,1,1,0,1)
        draw(0,0,0,0,1,1)
    }
    fun tile28(){
        draw(1,0,0,0,0,0)
        draw(0,2,0,0,1,0)
        draw(1,0,0,1,0,1)
        draw(0,2,0,1,1,1)
    }

    fun tile29(){
        draw(1,3,0,1,0,0)
        draw(1,3,1,1,1,0)
        draw(1,0,0,1,0,1)
        draw(0,0,0,0,1,1)
    }
    fun tile30(){
        draw(0,0,1,1,0,0)
        draw(1,0,1,0,1,0)
        draw(1,0,0,1,0,1)
        draw(0,0,0,0,1,1)
    }

    fun tile31(){
        draw(1,0,0,0,0,0)
        draw(0,0,0,1,1,0)
        draw(1,0,0,1,0,1)
        draw(0,0,0,0,1,1)
    }

    fun tile32(){
        tile30()
    }

    fun tile33(){
        draw(1,0,0,0,0,0)
        draw(1,0,1,0,1,0)
        draw(1,0,0,1,0,1)
        draw(0,0,0,0,1,1)
    }

    fun tile34(){
        draw(2,0,1,1,0,0)
        draw(1,3,1,1,1,0)
        draw(2,2,1,1,0,1)
        draw(1,0,1,1,1,1)
    }

    fun tile35(){
        draw(2,2,1,0,0,0)
        draw(0,0,0,1,1,0)
        draw(2,2,1,1,0,1)
        draw(1,0,1,1,1,1)
    }
    fun tile36(){
        draw(2,2,1,0,0,0)
        draw(1,0,1,0,1,0)
        draw(2,2,1,1,0,1)
        draw(1,0,1,1,1,1)
    }
    fun tile37(){
        draw(1,3,0,1,0,0)
        draw(1,3,1,1,1,0)
        draw(0,0,1,0,0,1)
        draw(1,0,1,1,1,1)
    }
    fun tile38(){
        draw(0,0,1,1,0,0)
        draw(0,0,0,1,1,0)
        draw(0,0,1,0,0,1)
        draw(1,0,1,1,1,1)
    }

    fun tile39(){
        tile23()
    }
    fun tile40(){
        draw(0,0,1,1,0,0)
        draw(1,0,1,0,1,0)
        draw(0,0,1,0,0,1)
        draw(1,0,1,1,1,1)
    }
    fun tile41(){
        draw(1,0,0,0,0,0)
        draw(1,0,1,0,1,0)
        draw(0,0,1,0,0,1)
        draw(1,0,1,1,1,1)
    }
    fun tile42(){
        draw(2,3,0,1,0,0)
        draw(2,3,1,1,1,0)
        draw(1,0,0,1,0,1)
        draw(1,0,1,1,1,1)
    }
    fun tile43(){
        draw(0,0,1,1,0,0)
        draw(0,0,0,1,1,0)
        draw(1,0,0,1,0,1)
        draw(1,0,1,1,1,1)
    }
    fun tile44(){
        draw(1,0,0,0,0,0)
        draw(0,0,0,1,1,0)
        draw(1,0,0,1,0,1)
        draw(1,0,1,1,1,1)
    }
    fun tile45(){
        draw(0,0,1,1,0,0)
        draw(1,0,1,0,1,0)
        draw(1,0,0,1,0,1)
        draw(1,0,1,1,1,1)
    }
    fun tile46(){
        batch.draw(wholeTiles[0][1],origin.x,origin.y)
    }
    fun tile47(){
        draw(2,0,1,1,0,0)
        draw(2,0,0,1,1,0)
        draw(2,0,1,0,0,1)
        draw(2,0,0,0,1,1)
    }

    fun draw(autoTileX:Int, autoTileY:Int, splitTileX:Int, splitTileY:Int, destinationXIndex:Int, destinationYIndex:Int){
        batch.draw(wholeTiles[autoTileY][autoTileX].split(halfSize.toInt(),halfSize.toInt())[splitTileY][splitTileX],
        origin.x+halfSize*destinationXIndex,origin.y+halfSize*(1-destinationYIndex))
    }
    fun subTile(autoTileX:Int, autoTileY:Int, splitTileX:Int, splitTileY:Int):TextureRegion{
        return wholeTiles[autoTileY][autoTileX].split(halfSize.toInt(),halfSize.toInt())[splitTileY][splitTileX]
    }

    fun prepDrawThen(x:Int, y:Int, action:()->Unit){
        batch.setProjectionMatrix(camera.combined)
        batch.begin()
        val worldX = x*tileSize.toFloat()
        val worldY = y*tileSize.toFloat()
        origin.add(worldX,worldY)
        action()
        origin.add(-worldX,-worldY)
        batch.end()
    }

    fun unpackAutoTiles() : TextureRegion{
        frameBuffer.begin();
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        prepDrawThen(0,0) {tile0() }
        prepDrawThen(1,0) {tile1() }
        prepDrawThen(2,0) {tile2() }
        prepDrawThen(3,0) {tile3() }
        prepDrawThen(4,0) {tile4() }
        prepDrawThen(5,0) {tile5() }
        prepDrawThen(6,0) {tile6() }
        prepDrawThen(7,0) {tile7() }
        prepDrawThen(8,0) {tile8() }
        prepDrawThen(9,0) {tile9() }
        prepDrawThen(10,0) {tile10() }
        prepDrawThen(11,0) {tile11() }
        prepDrawThen(12,0) {tile12() }
        prepDrawThen(13,0) {tile13() }
        prepDrawThen(14,0) {tile14() }
        prepDrawThen(15,0) {tile15() }
        prepDrawThen(16,0) {tile16() }
        prepDrawThen(17,0) {tile17() }
        prepDrawThen(18,0) {tile18() }
        prepDrawThen(19,0) {tile19() }
        prepDrawThen(20,0) {tile20() }
        prepDrawThen(21,0) {tile21() }
        prepDrawThen(22,0) {tile22() }

        prepDrawThen(23,0) {tile23() }
        prepDrawThen(24,0) {tile24() }
        prepDrawThen(25,0) {tile25() }
        prepDrawThen(26,0) {tile26() }

        prepDrawThen(27,0) {tile27() }
        prepDrawThen(28,0) {tile28() }
        prepDrawThen(29,0) {tile29() }
        prepDrawThen(30,0) {tile30() }
        prepDrawThen(31,0) {tile31() }
        prepDrawThen(32,0) {tile32() }
        prepDrawThen(33,0) {tile33() }
        prepDrawThen(34,0) {tile34() }
        prepDrawThen(35,0) {tile35() }
        prepDrawThen(36,0) {tile36() }
        prepDrawThen(37,0) {tile37() }
        prepDrawThen(38,0) {tile38() }

        prepDrawThen(39,0) {tile39() }
        prepDrawThen(40,0) {tile40() }
        prepDrawThen(41,0) {tile41() }
        prepDrawThen(42,0) {tile42() }
        prepDrawThen(43,0) {tile43() }
        prepDrawThen(44,0) {tile44() }
        prepDrawThen(45,0) {tile45() }
        prepDrawThen(46,0) {tile46() }
        prepDrawThen(47,0) {tile47() }

        frameBuffer.end();
        val result= TextureRegion(frameBuffer.colorBufferTexture)
        result.flip(false, true)
        return result
    }
}