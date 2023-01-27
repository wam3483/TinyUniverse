package com.pixelatedmind.game.tinyuniverse.screen

import com.badlogic.gdx.*
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils
import com.pixelatedmind.game.tinyuniverse.graph.VoronoiGraph
import com.pixelatedmind.game.tinyuniverse.graph.GenericVector2
import com.pixelatedmind.game.tinyuniverse.input.KeyboardCameraMoveProcessor
import com.pixelatedmind.game.tinyuniverse.input.ScrollZoomInputProcessor
import java.util.*

class VoronoiGraphViewer : ApplicationAdapter(), InputProcessor {
    lateinit var shapeRenderer : ShapeRenderer
    lateinit var camera : OrthographicCamera

    lateinit var voronoiGraph : VoronoiGraph<String>
    lateinit var sourceVoronoiGraph : VoronoiGraph<String>
    lateinit var dstVoronoiGraph : VoronoiGraph<String>

    var showPointCloud = false
    var showDelaunayGraph = false
    var showVoronoiGraph = true
    var showVoronoiVerticesGraph = true
    var showVoronoiCellCentroid = false

    fun reInit(){
        voronoiGraph = VoronoiGraph(getPointCloud(1000).map{ GenericVector2<String>("",it.x,it.y) })
        sourceVoronoiGraph = voronoiGraph
        repeat(5) {
            sourceVoronoiGraph = VoronoiGraph(sourceVoronoiGraph.getVoronoiCells().map { it.centroid() }.map { GenericVector2<String>("", it.x, it.y) })
        }
        dstVoronoiGraph = VoronoiGraph(sourceVoronoiGraph.getVoronoiCells().map{it.centroid()}.map{ GenericVector2<String>("",it.x,it.y) })
        sourceVoronoiGraph = voronoiGraph
        voronoiGraph = sourceVoronoiGraph
    }

    private fun getPointCloud(size:Int):List<Vector2>{
        val w = 400//Gdx.graphics.getWidth().toFloat()
        val h = 200//Gdx.graphics.getHeight().toFloat()
        val random = Random()
        var i = 0
        val pointCloud = mutableListOf<Vector2>()
        while(i<size){
            pointCloud.add(Vector2((random.nextDouble()*(w/2)).toFloat(), (random.nextDouble()*h).toFloat()))
            i++
        }
        return pointCloud
    }

    override fun create() {
        reInit()
        shapeRenderer = ShapeRenderer()
        shapeRenderer.setAutoShapeType(true)
        camera = OrthographicCamera(Gdx.graphics.getWidth().toFloat(), Gdx.graphics.getHeight().toFloat());

        val zoomInput = ScrollZoomInputProcessor(camera!!)
        val moveInput = KeyboardCameraMoveProcessor(camera!!)
        val multiplex = InputMultiplexer(zoomInput, moveInput, this)
        Gdx.input.inputProcessor = multiplex
    }

    fun renderVoronoi(){
        val edges = voronoiGraph.getEdges()
        var i = 0
        shapeRenderer.setColor(0f,0f,0f,1f)
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled)
        while(i<edges.size){
            shapeRenderer.line(edges[i].centroid(), edges[i+1].centroid())
            i+=2
        }
    }

    fun renderVoronoiVertices(){
        val edges = voronoiGraph.getEdges()
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0f,0.5f,.5f,.1f)
        edges.forEach{
            val centroid = it.centroid()
            shapeRenderer.circle(centroid.x,centroid.y,2.5f)
        }
    }

    fun renderDulaunay(){
        //render fully connected graph delaunay
        shapeRenderer.setColor(0f,.4f,.4f,.5f)
        shapeRenderer.set(ShapeRenderer.ShapeType.Line)
        val delaunayEdges = voronoiGraph.delaunayGraph.getEdges()
        var i = 0
        while(i < delaunayEdges.size){
            shapeRenderer.line(delaunayEdges[i],
                    delaunayEdges[i+1])
            shapeRenderer.line(delaunayEdges[i],
                    delaunayEdges[i+2])
            shapeRenderer.line(delaunayEdges[i+1],
                    delaunayEdges[i+2])
            i+=3
        }
    }

    fun renderVoronoiCenters(){
        shapeRenderer.setColor(.6f,0.6f,0f,1f)
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled)
        voronoiGraph.getVoronoiCells().forEach{cell->
            val edges = cell.getEdges()
            var i = 0
            while(i<edges.size){
                shapeRenderer.line(edges[i].centroid(), edges[i+1].centroid())
                i += 2
            }
        }
    }

    fun renderPointCloud(){
        //render blue dots for point cloud
        shapeRenderer.setColor(.6f,0f,0f,1f)
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled)
        voronoiGraph.delaunayGraph.vertices.forEach{
            shapeRenderer.circle(it.x,it.y,2.5f)
        }
    }

    override fun render() {
        ScreenUtils.clear(0.25f, 0.25f, 0.25f, 1f)
        camera.update()
        shapeRenderer.setProjectionMatrix(camera!!.combined)

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        shapeRenderer.begin()
        if(showVoronoiGraph) {
            renderVoronoi()
        }
        if(showVoronoiVerticesGraph){
            renderVoronoiVertices()
        }
        if(showDelaunayGraph) {
            renderDulaunay()
        }
        if(showPointCloud) {
            renderPointCloud()
        }
        if(showVoronoiCellCentroid) {
            renderVoronoiCenters()
        }
        if(this.voronoiGraph == sourceVoronoiGraph){

            shapeRenderer.setColor(1f,0f,1f,1f)
            shapeRenderer.circle(0f,0f,10f)
        }else {
            shapeRenderer.setColor(1f,1f,1f,1f)
            shapeRenderer.circle(0f,0f,10f)
        }
        shapeRenderer.end()
    }

    override fun keyDown(keycode: Int): Boolean {
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        if(keycode == Input.Keys.ENTER){
            reInit()
            return true
        }else if(keycode == Input.Keys.NUM_1){
            showVoronoiGraph = !showVoronoiGraph
        }else if(keycode == Input.Keys.NUM_2){
            showDelaunayGraph = !showDelaunayGraph
        }
        else if(keycode == Input.Keys.NUM_3){
            showPointCloud = !showPointCloud
        }
        else if(keycode == Input.Keys.NUM_4){
            showVoronoiVerticesGraph = !showVoronoiVerticesGraph
        }
        else if(keycode == Input.Keys.NUM_5){
            showVoronoiCellCentroid = !showVoronoiCellCentroid
        }else if(keycode == Input.Keys.Z){
            if(this.voronoiGraph == sourceVoronoiGraph){
                this.voronoiGraph = dstVoronoiGraph
            }else {
                this.voronoiGraph = sourceVoronoiGraph
            }
        }
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return false
    }
}