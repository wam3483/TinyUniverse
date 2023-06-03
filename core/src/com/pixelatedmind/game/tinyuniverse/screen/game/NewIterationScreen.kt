package com.pixelatedmind.game.tinyuniverse.screen.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ScreenUtils
import com.pixelatedmind.game.tinyuniverse.animation.CRTPowerOnAnimation
import com.pixelatedmind.game.tinyuniverse.extensions.color.darker
import com.pixelatedmind.game.tinyuniverse.util.SimpleTimer
import space.earlygrey.shapedrawer.ShapeDrawer
import java.util.*


class NewIterationScreen : ScreenAdapter {
    private val rootPrompt = "THXSEN:\\>"
    private val startFontColor = Color.BLACK.cpy()
    private val powerOnFontColor = Color(.5f,1f,.5f,1f)
    private val endFontColor = Color.GREEN.cpy()

    private val camera : OrthographicCamera
    private val font : BitmapFont
    private val layout : GlyphLayout
    private val batch : SpriteBatch
    private lateinit var drawer : ShapeDrawer

    private val cursor = CursorData()
    private val playTypeAnimation = TypeAnimation()
    private val periodTypeAnimation = TypeAnimation()
    val periodTypeSpeedTimer : SimpleTimer

    private val music : Music
    private var startMusic = false

    private val powerOn : CRTPowerOnAnimation

    constructor(font : BitmapFont, width : Float, height: Float, iterations : Int, seed : Long) {
        this.font = font
        this.layout = GlyphLayout()
        camera = OrthographicCamera()
        camera.setToOrtho(true, width, height)
        batch = SpriteBatch()
        initShapeDrawer()
        powerOn = CRTPowerOnAnimation(width, height, drawer)

        music = Gdx.audio.newMusic(Gdx.files.internal("music/crtPowerOn.ogg"))

        periodTypeSpeedTimer = SimpleTimer(1f, {
            periodTypeAnimation.characterTypeSpeedSecs /= 2
        })

        playTypeAnimation.textToType = generateIterationText(iterations, seed)
        playTypeAnimation.delaySecsAfterTyping = 0f

        periodTypeAnimation.textToType = ".".repeat(2000)
        periodTypeAnimation.delaySecsBeforeTyping = 1f
        periodTypeAnimation.characterTypeSpeedSecs = .1f
    }

    fun generateIterationText(iterations : Int, seed : Long) : String {
        val builder = StringBuilder()
        repeat(iterations){
            builder.append("play(")
        }
        builder.append(seed)
        repeat(iterations){
            builder.append(")")
        }
        builder.append(";")
        return builder.toString()
    }

    fun initShapeDrawer(){
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        val color = 0xFFFFFFFF
        pixmap.setColor(color.toInt())
        pixmap.drawPixel(0, 0)
        val texture = Texture(pixmap) //remember to dispose of later

        pixmap.dispose()
        val region = TextureRegion(texture, 0, 0, 1, 1)
        drawer = ShapeDrawer(batch, region)
    }

    override fun render(delta: Float) {
        if(!startMusic){
            music.play()
            startMusic = true
        }
        ScreenUtils.clear(0f,0f,0f,1f)
        camera.update()

        powerOn.render(delta)

        cursor.update(delta)
        if(!playTypeAnimation.animationComplete) {
            playTypeAnimation.update(delta)
        }else{
            periodTypeSpeedTimer.update(delta)
            periodTypeAnimation.update(delta)
        }

        batch.begin()
        this.layout.setText(font,getConsoleText(),
                getFontColor(delta),
                camera.viewportWidth, Align.left,true)
        font.draw(batch, layout, 0f,camera.viewportHeight)
        batch.end()

    }

    private var powerOnFontAnimationElapsedSecs = 0f
    private fun getFontColor(delta : Float) : Color{
        powerOnFontAnimationElapsedSecs += delta
        if(powerOnFontAnimationElapsedSecs < playTypeAnimation.delaySecsBeforeTyping){
            val halfDuration = playTypeAnimation.delaySecsBeforeTyping / 2
            if(powerOnFontAnimationElapsedSecs < halfDuration){
                return startFontColor.lerp(powerOnFontColor, powerOnFontAnimationElapsedSecs / halfDuration)
            }else if(powerOnFontAnimationElapsedSecs<playTypeAnimation.delaySecsBeforeTyping){
                val delta = (powerOnFontAnimationElapsedSecs - halfDuration) / halfDuration
                return powerOnFontColor.lerp(endFontColor, delta)
            }
        }
        return endFontColor
    }

    private fun getConsoleText() : String {
        var result = rootPrompt
        result += playTypeAnimation.getText()
        if(playTypeAnimation.animationComplete){
            result+= "\n"+rootPrompt
        }
        result += periodTypeAnimation.getText()
        if(cursor.visible){
            result += "_"
        }
        return result
    }

    override fun dispose() {
        super.dispose()
        music.dispose()
    }

    class TypeAnimation{
        var delaySecsBeforeTyping = 1.6f
        var delaySecsAfterTyping = 5f

        var characterTypeSpeedSecs = .25f
        var textToType = ""
        var elapsedTypeSpeedSecs = 0f
        var animationComplete = false

        val delayChance = .5
        val random = Random()

        fun getText() : String {
            val time = elapsedTypeSpeedSecs - delaySecsBeforeTyping
            if(time < characterTypeSpeedSecs){
                return ""
            }
            val charactersToType = (time / characterTypeSpeedSecs).toInt()
            if(charactersToType < textToType.length){
                return textToType.substring(0, charactersToType)
            }
            return textToType
        }

        fun getAnimationTime() : Float{
            return delaySecsBeforeTyping + delaySecsAfterTyping + textToType.length * characterTypeSpeedSecs
        }

        fun update(deltaSecs : Float){
            val chance = random.nextDouble()
            if(chance>delayChance) {
                elapsedTypeSpeedSecs += deltaSecs/10f
            }else if(chance <= delayChance){
                elapsedTypeSpeedSecs += deltaSecs*2
            }
            animationComplete = elapsedTypeSpeedSecs >= getAnimationTime()
        }
    }
    class CursorData{
        val blinkFrequencySecs = .5f
        var visible = false
        var elapsedBlinkStateSecs = 0f

        fun update(deltaSecs : Float){
            elapsedBlinkStateSecs += deltaSecs
            if(elapsedBlinkStateSecs > blinkFrequencySecs){
                visible = !visible
                elapsedBlinkStateSecs %= blinkFrequencySecs
            }
        }
    }
}