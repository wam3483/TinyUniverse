package com.pixelatedmind.game.tinyuniverse.util

import com.badlogic.gdx.math.Rectangle
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

//https://gamedevelopment.tutsplus.com/tutorials/how-to-use-tile-bitmasking-to-auto-tile-your-level-layouts--cms-25673
//followed the above tutorial to create an expanded 49 images from a 12 tile autotile image.
//this is necessary because we're autogenerating maps. makes creating more complex tiled maps simpler.
class AutoTileExpanderUtil {

    lateinit var autoTileImg : BufferedImage
    var tileSize = 32
    lateinit var g : Graphics2D

    fun load(file: File,tileSize:Int) {
        val resultImg = BufferedImage((48+1)*tileSize,tileSize,BufferedImage.TYPE_4BYTE_ABGR)
        g = resultImg.createGraphics()
        autoTileImg = ImageIO.read(file)

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
        prepDrawThen(48,0) {tile48() }
        ImageIO.write(resultImg, "png",File("test.png"))
    }

    fun prepDrawThen(x:Int, y:Int, action:()->Unit){
        val worldX = x*tileSize
        val worldY = y*tileSize
        g.translate(worldX,worldY)
        action()
        g.color = Color.RED
//        g.drawString(x.toString(), tileSize/2,tileSize/2)
        g.translate(-worldX,-worldY)
    }

    fun draw(autoTileX:Int, autoTileY:Int, splitTileX:Int, splitTileY:Int, destinationXIndex:Int, destinationYIndex:Int){
        g.graphicsDraw(autoTileImg,tileSize,autoTileX,autoTileY,splitTileX,splitTileY,destinationXIndex,destinationYIndex)
    }

    fun drawWholeTile(autoTileX:Int, autoTileY:Int, destinationXIndex:Int, destinationYIndex:Int){
        var x = autoTileX * tileSize
        var y = autoTileY * tileSize
        var dx = destinationXIndex * tileSize
        var dy = destinationYIndex * tileSize
        g.drawImage(autoTileImg, dx,dy,dx+tileSize,dy+tileSize,x,y,x+tileSize,y+tileSize,null)
    }

    fun Graphics2D.graphicsDraw(img:BufferedImage, tileSize:Int, autoTileX:Int, autoTileY:Int, splitTileX:Int, splitTileY:Int, destinationXIndex:Int, destinationYIndex:Int){
        val halfSize = tileSize/2
        var x = autoTileX*tileSize + splitTileX * halfSize
        var y = autoTileY*tileSize + splitTileY * halfSize
        val dx = destinationXIndex * halfSize
        val dy = destinationYIndex * halfSize
        this.drawImage(img, dx,dy,dx+halfSize,dy+halfSize,x,y,x+halfSize,y+halfSize,null)
    }

    fun tile0(){
        drawWholeTile(1,0,0,0)
    }

    fun tile1(){
        draw(2,2,1,0,0,0)
        draw(0,2,0,0,1,0)
        draw(2,0,0,0,1,1)
        draw(2,0,1,0,0,1)
    }

    fun tile2(){
        draw(1,3,0,1,0,0)
        draw(2,0,0,1,1,0)
        draw(1,1,1,0,0,1)
        draw(2,0,0,0,1,1)
    }

    fun tile3(){
        draw(0,0,1,1,0,0)
        draw(0,2,0,0,1,0)
        draw(2,0,0,0,1,1)
        draw(1,1,0,0,0,1)
    }

    fun tile4(){
        draw(1,0,0,0,0,0)
        draw(0,2,0,0,1,0)
        draw(1,1,0,0,0,1)
        draw(2,0,0,0,1,1)
    }

    fun tile5(){
        draw(2,0,1,1,0,0)
        draw(1,3,1,1,1,0)
        draw(2,0,1,0,0,1)
        draw(1,1,1,0,1,1)
    }
    fun tile6(){
        draw(2,2,1,1,0,0)
        draw(0,0,0,1,1,0)
        draw(2,0,1,0,0,1)
        draw(1,1,0,0,1,1)
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
        draw(0,0,1,1,0,0)
        draw(0,0,0,1,1,0)
        draw(1,1,0,0,0,1)
        draw(1,1,1,0,1,1)
    }

    fun tile10(){
        draw(1,0,1,1,0,0)
        draw(0,0,0,1,1,0)
        draw(1,1,0,0,0,1)
        draw(1,1,1,0,1,1)
    }

    fun tile11(){
        draw(1,1,0,0,0,1)
        draw(1,1,1,0,1,1)
        draw(0,0,1,1,0,0)
        draw(1,0,0,0,1,0)
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
        draw(1,3,0,1,0,0)
        draw(2,0,0,1,1,0)
        draw(1,0,0,1,0,1)
        draw(0,2,0,1,1,1)
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
        draw(1,0,1,0,1,1)
        draw(0,0,1,0,0,1)
    }

    fun tile42(){
        draw(1,3,0,1,0,0)
        draw(1,3,1,1,1,0)
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
        drawWholeTile(1, 0, 0,0)
    }
    fun tile47(){
        drawWholeTile(1, 0, 0, 0)
    }
    fun tile48(){
        drawWholeTile(1, 2, 0, 0)
    }
}