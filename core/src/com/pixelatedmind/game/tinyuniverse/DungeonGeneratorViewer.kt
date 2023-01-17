package com.pixelatedmind.game.tinyuniverse

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils
import com.pixelatedmind.game.tinyuniverse.generation.*

class DungeonGeneratorViewer : ApplicationAdapter {
    lateinit var batch: SpriteBatch
    var img: Texture? = null
    val generator = DungeonGenerator(50, 30f, 10f, Vector2(3f, 3f))//,-3302475581927919216)//, 8633074958013025850)
    lateinit var shapeRenderer: ShapeRenderer
    var camera : OrthographicCamera? = null
    lateinit var font : BitmapFont

    var regionModel : RegionModel? = null

    var generationComplete = false

    constructor(){

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

    override fun render() {
        ScreenUtils.clear(0f, 0f, 0f, 1f)
        camera!!.update()
        shapeRenderer.setProjectionMatrix(camera!!.combined)
        batch.setProjectionMatrix(camera!!.combined)

        val rooms = mutableListOf<Rectangle>()
        if(generationComplete){
            shapeRenderer.setColor(0f,1f,0f,1f)
            rooms.addAll(generator.getMainRooms())
        }

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        generator.getAllRooms().forEach{rect->
            shapeRenderer.begin()
            if(rooms.contains(rect)){
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
        generator.update(Gdx.graphics.deltaTime)
        if(!generationComplete && generator.isGenerationComplete()){
            val mainRooms = generator.getMainRooms()
            val potentialSubRooms = generator.getAllRooms().filter{!mainRooms.contains(it)}
            regionModel = generator.newMainRoomGraph(mainRooms, potentialSubRooms)
            generationComplete = true
        }
    }

    override fun dispose() {
        batch!!.dispose()
        img!!.dispose()
    }
}