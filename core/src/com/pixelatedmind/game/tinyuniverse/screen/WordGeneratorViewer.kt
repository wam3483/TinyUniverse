package com.pixelatedmind.game.tinyuniverse.screen

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils
import com.pixelatedmind.game.tinyuniverse.generation.world.*
import com.pixelatedmind.game.tinyuniverse.generation.world.mapper.WorldPolygonModelGraphMapper
import com.pixelatedmind.game.tinyuniverse.generation.world.model.WorldModel
import com.pixelatedmind.game.tinyuniverse.generation.world.model.WorldPolygonModel
import com.pixelatedmind.game.tinyuniverse.graph.DelaunayVoronoiGraph
import com.pixelatedmind.game.tinyuniverse.graph.DelaunayVoronoiGraphBuilder
import com.pixelatedmind.game.tinyuniverse.graph.Graph
import com.pixelatedmind.game.tinyuniverse.input.KeyboardCameraMoveProcessor
import com.pixelatedmind.game.tinyuniverse.input.ScrollZoomInputProcessor
import com.pixelatedmind.game.tinyuniverse.datastructure.Bitmap
import com.pixelatedmind.game.tinyuniverse.maps.tiled.NoiseBitmap
import com.pixelatedmind.game.tinyuniverse.maps.tiled.TiledBitmapDecorator
import com.pixelatedmind.game.tinyuniverse.renderer.WorldModelRenderer
import com.pixelatedmind.game.tinyuniverse.renderer.WorldModelRendererConfig
import hoten.perlin.Perlin2d
import space.earlygrey.shapedrawer.ShapeDrawer
import java.util.*


class WordGeneratorViewer : ApplicationAdapter() {
    lateinit var camera : OrthographicCamera
    lateinit var shapeDrawer : ShapeDrawer
    lateinit var shapeRenderer : ShapeRenderer
    lateinit var dvGraph : DelaunayVoronoiGraph
    lateinit var graph : Graph<WorldPolygonModel>
    lateinit var batch : SpriteBatch
    lateinit var random:Random

    lateinit var worldRenderer : WorldModelRenderer

    val randomGen = Random()


    override fun create() {
        val w = Gdx.graphics.width.toFloat()
        val h = Gdx.graphics.height.toFloat()

        camera = OrthographicCamera()
        camera.setToOrtho(false, w, h)
        val zoomInput = ScrollZoomInputProcessor(camera!!)
        val moveInput = KeyboardCameraMoveProcessor(camera!!)
        val multiplex = InputMultiplexer(zoomInput, moveInput)
        Gdx.input.inputProcessor = multiplex
        initShapeDrawer()
        reinit()

    }

    fun reinit(){
        var seed = 1944432210214770054//randomGen.nextLong()
//        seed = 1412840536380701127 //<small path for waterline
//        seed = 254921089064146843 //<-- nice looking world
        println("seed = "+seed)
        random = Random(seed)

        graph = generateWorldModelGraph()
        worldRenderer = WorldModelRenderer(this.shapeDrawer, WorldModel(graph), WorldModelRendererConfig())
    }

    private fun getPointCloud(size:Int, w:Int, h:Int):List<Vector2>{
        var i = 0
        val pointCloud = mutableListOf<Vector2>()
        while(i<size){
            pointCloud.add(Vector2((random.nextDouble()*(w)).toFloat(), (random.nextDouble()*h).toFloat()))
            i++
        }
        return pointCloud
    }

    fun initShapeDrawer(){
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        val color = 0xFFFFFFFF
        pixmap.setColor(color.toInt())
        pixmap.drawPixel(0, 0)
        val texture = Texture(pixmap) //remember to dispose of later

        pixmap.dispose()
        val region = TextureRegion(texture, 0, 0, 1, 1)
        batch = SpriteBatch()
        shapeDrawer = ShapeDrawer(batch, region)
        shapeRenderer = ShapeRenderer()
    }

    fun generateWorldModelGraph() : Graph<WorldPolygonModel> {
        val lineSeed = random.nextLong()
        val lineRandom = Random(lineSeed)
        println("lineseed = "+lineSeed)
        val graphBuilder = DelaunayVoronoiGraphBuilder()
        val pointCloudW = 500
        val pointCloudH = 500
        dvGraph = graphBuilder.buildDelaunayVoronoiGraph(getPointCloud(1000,pointCloudW,pointCloudH))
        val iterations = 3
        repeat(iterations) {
            dvGraph = graphBuilder.buildDelaunayVoronoiGraph(dvGraph.getCenterOfVoronoiSites())
        }
//        val lineRandom = Random(random.nextLong())
//        val lineInterpolator = NullLineInterpolator()
        val lineInterpolator = QuadrilateralLineInterpolaterImpl(lineRandom)
        val bitmap = landmassBitmap(random.nextInt(), pointCloudW, pointCloudH, .5)
        val worldModelGraphMapper = WorldPolygonModelGraphMapper(lineInterpolator, bitmap, random)
        val graph = worldModelGraphMapper.map(dvGraph)
        return graph
    }

    fun renderVoronoiGraph(){
        val dVerts = dvGraph.flattenDelaunayVertices()
        shapeDrawer.batch.begin()

//        shapeDrawer.setColor(Color.BLACK)
//        dVerts.forEach{
//            val edges = dvGraph.getEdges(it)!!
//            edges.forEach{edge->
//                shapeDrawer.line(edge.delaunayN1,edge.delaunayN2)
//            }
//        }
//
        shapeDrawer.setColor(Color.RED)
        dVerts.forEach{
            shapeDrawer.filledCircle(it,3f)
        }

        shapeDrawer.setColor(Color.WHITE)
        dVerts.forEach{
            val edges = dvGraph.getEdges(it)!!
            edges.forEach{edge->
                if(edge.voronoiN1!=null && edge.voronoiN2!=null) {
                    shapeDrawer.line(edge.voronoiN1, edge.voronoiN2)
                }
            }
        }

        shapeDrawer.setColor(Color.BLUE)
        dVerts.forEach{
            val edges = dvGraph.getEdges(it)!!
            edges.forEach{edge->
                if(edge.voronoiN1!=null && edge.voronoiN2!=null) {
                    shapeDrawer.filledCircle(edge.voronoiN1,2f)
                    shapeDrawer.filledCircle(edge.voronoiN2,2f)
                }
            }
        }
        shapeDrawer.batch.end()
    }

    fun landmassBitmap(seed:Int, width:Int, height:Int, noiseThreshold:Double): Bitmap {
        val persistence = .5
        val octaves = 8
        val noise = Perlin2d(persistence, octaves, seed)
        val ary = noise.createTiledArray(width, height)
        val map = ary.map{it.toList()}
        return TiledBitmapDecorator(NoiseBitmap(map, noiseThreshold))
    }

    var timeAccumulatorSecs: Float = 0f
    val updateMapFrequency : Float = 10f
    override fun render() {
        timeAccumulatorSecs += Gdx.graphics.deltaTime
        if(timeAccumulatorSecs>=updateMapFrequency){
            reinit()
            timeAccumulatorSecs = 0f
        }
        ScreenUtils.clear(.4f, .4f, .4f, 1f)
        camera.update()
        batch.projectionMatrix = camera!!.combined
        worldRenderer.render()
//        renderVoronoiGraph()
    }
}