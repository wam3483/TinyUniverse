package com.pixelatedmind.game.tinyuniverse

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.ScreenUtils
import com.pixelatedmind.game.tinyuniverse.generation.*
import java.util.*

class DungeonGeneratorViewer : ApplicationAdapter {
    lateinit var batch: SpriteBatch
    var img: Texture? = null
    lateinit var generator : DungeonGenerator// -3302475581927919216 8633074958013025850
    lateinit var shapeRenderer: ShapeRenderer
    var camera : OrthographicCamera? = null
    lateinit var font : BitmapFont

    var regionModel : RegionModel? = null


    constructor(){
        init()
    }

    private fun init(){
        val random = newRandom(null)//8633074958013025850)
        val positionFactory = VectorFactoryEllipseImpl(10f,10f, random)
        val rectFactory = RectangleFactoryNormalDistributionImpl(positionFactory, random,30f,10f,3f,3f)
        val rectSeparationDecorator = RectangleFactorySeparationDecoratorImpl(rectFactory)
        generator = DungeonGenerator(rectSeparationDecorator, 50, random)
        regionModel = generator.newMainRoomGraph()
    }


    private fun newRandom(seed:Long?) : Random{
        val inputSeed = seed ?: Random().nextLong()
        return Random(inputSeed)
    }

    override fun create() {
        batch = SpriteBatch()
        shapeRenderer = ShapeRenderer()
        shapeRenderer.setAutoShapeType(true)
        camera = OrthographicCamera(Gdx.graphics.getWidth().toFloat(),Gdx.graphics.getHeight().toFloat());
        img = Texture("badlogic.jpg")
        camera!!.zoom = 1F
        font = BitmapFont()
    }

    var timeAccumulatorSecs = 0f
    var newDungeonDelaySecs = 10f

    override fun render() {
        timeAccumulatorSecs += Gdx.graphics.deltaTime;
        if(timeAccumulatorSecs>=newDungeonDelaySecs){
            init()
            timeAccumulatorSecs = 0f
        }
        ScreenUtils.clear(0f, 0f, 0f, 1f)
        camera!!.update()
        shapeRenderer.setProjectionMatrix(camera!!.combined)
        batch.setProjectionMatrix(camera!!.combined)

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        regionModel!!.allRooms.forEach{rect->
            shapeRenderer.begin()
            if(regionModel!!.mainRoomGraph.getAllValues().contains(rect)){
                shapeRenderer.setColor(0f, 1f, 0f, 1f)
            }
            else if(regionModel!=null && regionModel!!.subrooms.contains(rect)){
                shapeRenderer.setColor(1f, 1f, 1f, 1f)
            }
            else{
                shapeRenderer.setColor(0f,0f,1f,.2f)
            }
            shapeRenderer.rect(rect.x,rect.y,rect.width,rect.height)
            shapeRenderer.end()
        }
        Gdx.gl.glDisable(GL20.GL_BLEND)

        if(regionModel != null){
//            shapeRenderer.begin()
//            val stack = Stack<Rectangle>()
//            stack.add(regionModel!!.fullyConnectedMainRoomPreGenGraph!!.nodes[0])
//            val visited = mutableSetOf<Rectangle>()
//            while(stack.isNotEmpty()){
//                val rect = stack.pop()
//                val children = regionModel!!.fullyConnectedMainRoomPreGenGraph!!.getChildren(rect)
//                visited.add(rect)
//                children?.forEach {
//                    if(!visited.contains(it.value)) {
//                        shapeRenderer.setColor(1f,0f,0f,1f)
//                        shapeRenderer.line(rect.x+rect.width/2,rect.y+rect.height/2,it.value.x+it.value.width/2,it.value.y+it.value.height/2)
//                        stack.push(it.value)
//                    }
//                }
//            }
//            shapeRenderer.end()

//            shapeRenderer.begin()
//            shapeRenderer.setColor(0f, 0f, 1f, .5f)
//            shapeRenderer.set(ShapeRenderer.ShapeType.Filled)
//            var v1 = Vector2()
//            var v2 = Vector2()
//            regionModel!!.mainRoomGraph!!.edges.forEach { edge ->
//                edge.n1.value.getCenter(v1)
//                edge.n2.value.getCenter(v2)
//                shapeRenderer.circle(v1.x,v1.y,2f)
//                shapeRenderer.circle(v2.x,v2.y,2f)
//                shapeRenderer.line(v1, v2)
//            }
//            shapeRenderer.end()


            shapeRenderer.begin()
            shapeRenderer.setColor(1f, 1f, 0f, 1f)
            regionModel!!.hallways.forEach{shapeRenderer.rect(it.x,it.y,it.width,it.height)}
            shapeRenderer.end()

            shapeRenderer.begin()
            shapeRenderer.setColor(0f, 0f, 1f, 1f)
            shapeRenderer.set(ShapeRenderer.ShapeType.Filled)
            regionModel!!.doors.forEach{shapeRenderer.circle(it.x,it.y,2f)}
            shapeRenderer.end()
        }
    }

    override fun dispose() {
        batch!!.dispose()
        img!!.dispose()
    }
}