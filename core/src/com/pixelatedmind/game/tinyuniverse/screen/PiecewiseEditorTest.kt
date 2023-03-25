package com.pixelatedmind.game.tinyuniverse.screen

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.ScreenUtils
import com.pixelatedmind.game.tinyuniverse.ui.PiecewiseModel
import com.pixelatedmind.game.tinyuniverse.ui.PiecewiseFunctionEditPanel
import com.pixelatedmind.game.tinyuniverse.services.InterpolationFactory

class PiecewiseEditorTest : ApplicationAdapter() {
    lateinit var stage : Stage

    private fun color(r : Int, g : Int, b : Int, a: Int): Color {
        return Color(r/255f, g/255f,b/255f,a/255f)
    }

    override fun create() {
        val skin = Skin(Gdx.files.internal("skins/neonui/neon-ui.json"))
        val w = Gdx.graphics.width.toFloat()
        val h = Gdx.graphics.height.toFloat()

        stage = Stage()
        val model =
                PiecewiseModel(listOf<PiecewiseModel.Piece>(
                        PiecewiseModel.Piece(Vector2(0f, 0f), "sine", Interpolation.sine),
                        PiecewiseModel.Piece(Vector2(.25f, .6f), "sine", Interpolation.sine),
                        PiecewiseModel.Piece(Vector2(.5f, .8f), "sine", Interpolation.sine),
                        PiecewiseModel.Piece(Vector2(1f, 0f), "linear", Interpolation.linear)
                ))
//        val piecewiseEditor = PiecewiseFunctionActor(model)
        val pEditor = PiecewiseFunctionEditPanel(model, skin, InterpolationFactory())
        pEditor.width = 300f
        pEditor.height = 300f
        val table = Table()
        table.pad(100f)
        table.setFillParent(true)
        val group = HorizontalGroup()
        group.addActor(pEditor)
        group.fill()

        table.add(pEditor).pad(10f)
        stage.addActor(table)
//        table.debug = true
        table.layout()
        Gdx.input.setInputProcessor(stage)
        super.create()
    }

    private fun interpolationMapper(interpolationName : String) : Interpolation{
        val map = mapOf(
            "linear" to Interpolation.linear,
            "bounce" to Interpolation.bounce,"bounceIn" to Interpolation.bounceIn,"bounceOut" to Interpolation.bounceOut,
            "circle" to Interpolation.circle,"circleIn" to Interpolation.circleIn,"circleOut" to Interpolation.circleOut,
            "elastic" to Interpolation.elastic,"elasticIn" to Interpolation.elasticIn,"elasticOut" to Interpolation.elasticOut,
            "exp10" to Interpolation.exp10,"exp10In" to Interpolation.exp10In,"exp10Out" to Interpolation.exp10Out,
            "exp5" to Interpolation.exp5,"exp5In" to Interpolation.exp5In,
            "fade" to Interpolation.fade,"fastSlow" to Interpolation.fastSlow,"slowFast" to Interpolation.slowFast,
            "pow2" to Interpolation.pow2,"pow2In" to Interpolation.pow2In, "pow2InInverse" to Interpolation.pow2InInverse, "pow2Out" to Interpolation.pow2Out,"pow2OutInverse" to Interpolation.pow2OutInverse,
            "pow3" to Interpolation.pow3,"pow3In" to Interpolation.pow3In,"pow3InInverse" to Interpolation.pow3InInverse,"pow3Out" to Interpolation.pow3Out,"pow3OutInverse" to Interpolation.pow3OutInverse,
            "pow4" to Interpolation.pow4, "pow4In" to Interpolation.pow4In,"pow4Out" to Interpolation.pow4Out,
            "pow5" to Interpolation.pow5,"pow5In" to Interpolation.pow5In,"pow5Out" to Interpolation.pow5Out,
            "sine" to Interpolation.sine,"sineIn" to Interpolation.sineIn,"sineOut" to Interpolation.sineOut,
            "smooth" to Interpolation.smooth,"smooth2" to Interpolation.smooth2,"smoother" to Interpolation.smoother,
            "swing" to Interpolation.swing,"swingIn" to Interpolation.swingIn,"swingOut" to Interpolation.swingOut
        )
        return map[interpolationName]!!
    }

    override fun render() {
        ScreenUtils.clear(.2f, .2f, .2f, 1f)
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f))
        stage.draw()
    }
}