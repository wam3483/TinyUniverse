package com.pixelatedmind.game.tinyuniverse

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils
import com.pixelatedmind.game.tinyuniverse.flocking.GenericBoidImpl
import com.pixelatedmind.game.tinyuniverse.generation.Edge
import com.pixelatedmind.game.tinyuniverse.generation.Node
import com.pixelatedmind.game.tinyuniverse.generation.TriangleIndexGraph
import com.pixelatedmind.game.tinyuniverse.generation.WorldCellGenerator
import java.util.*

class TinyUniverseGame : ApplicationAdapter {
    lateinit var batch: SpriteBatch
    var img: Texture? = null
    val generator = WorldCellGenerator(50, 20f, 5f, Vector2(3f, 3f))
    lateinit var shapeRenderer: ShapeRenderer
    var camera : OrthographicCamera? = null
    lateinit var font : BitmapFont

    var fullyconnectedGraph : TriangleIndexGraph<Rectangle>? = null
    var roomGraph : List<Edge<Rectangle>>? = null

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
            rooms.addAll(generator.getRooms())
        }
        generator.boids.forEach{
            shapeRenderer.begin()
            val rect = it.value
            if(rooms.contains(rect)){
                shapeRenderer.setColor(0f, 1f, 0f, 1f)
            }else{
                shapeRenderer.setColor(1f,1f,1f,1f)
            }
            shapeRenderer.rect(rect.x,rect.y,rect.width,rect.height)
            shapeRenderer.end()
        }
        if(fullyconnectedGraph != null){
            shapeRenderer.begin()
            val stack = Stack<Rectangle>()
            stack.add(fullyconnectedGraph!!.nodes[0])
            val visited = mutableSetOf<Rectangle>()
            while(stack.isNotEmpty()){
                val rect = stack.pop()
                val children = fullyconnectedGraph!!.getChildren(rect)
                visited.add(rect)
                children?.forEach {
                    if(!visited.contains(it.value)) {
                        shapeRenderer.setColor(1f,0f,0f,1f)
                        shapeRenderer.line(rect.x+rect.width/2,rect.y+rect.height/2,it.value.x+it.value.width/2,it.value.y+it.value.height/2)
                        stack.push(it.value)
                    }
                }
            }
            shapeRenderer.end()

            if(roomGraph!=null) {
                shapeRenderer.begin()
                shapeRenderer.setColor(0f, 0f, 1f, 1f)
                var v1 = Vector2()
                var v2 = Vector2()
                roomGraph!!.forEach { edge ->
                    shapeRenderer.line(edge.n1.value.getCenter(v1), edge.n2.value.getCenter(v2))
                }
                shapeRenderer.end()
            }
        }
        generator.update(Gdx.graphics.deltaTime)
        if(!generationComplete && generator.isGenerationComplete()){
            fullyconnectedGraph = generator.newDelunuaryGraphFrom(generator.getRooms())
            roomGraph = generator.newMainRoomGraph(generator.getRooms())
            generationComplete = true
        }
    }

    override fun dispose() {
        batch!!.dispose()
        img!!.dispose()
    }
}