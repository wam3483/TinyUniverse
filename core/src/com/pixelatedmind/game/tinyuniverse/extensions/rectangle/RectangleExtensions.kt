package com.pixelatedmind.game.tinyuniverse.extensions.rectangle

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

    fun Rectangle.getSharedEdge(other:Rectangle) : LineSegment?{
        val xOverlap = xAxisOverlap(other)
        val yOverlap = yAxisOverlap(other)
        if(top()==other.y && xOverlap!=null){
            return LineSegment(Vector2(xOverlap.min,other.y), Vector2(xOverlap.max, other.y))
        }else if(y==other.top() && xOverlap!=null){
            return LineSegment(Vector2(xOverlap.min,y),Vector2(xOverlap.max,y))
        }else if(right() == other.x && yOverlap!=null){
            return LineSegment(Vector2(other.x,yOverlap.min),Vector2(other.x,yOverlap.max))
        }else if(x==other.right() && yOverlap!=null){
            return LineSegment(Vector2(x,yOverlap.min),Vector2(x,yOverlap.max))
        }
        return null
    }

    fun Rectangle.right() : Float{
        return x+width
    }

    fun Rectangle.top() : Float{
        return y+height
    }

    fun Rectangle.midX():Float{
        return x+width/2
    }

    fun Rectangle.midY():Float{
        return y+height/2
    }

    fun Rectangle.yAxisOverlap(other: Rectangle): Range?{
        if(y>other.top() || top()<other.y){
            return null
        }
        return Range(
                y.coerceAtLeast(other.y),
                top().coerceAtMost(other.top())
        )
    }

    fun Rectangle.xAxisOverlap(other: Rectangle) : Range?{
        if(x>other.right() || right()< other.x){
            return null
        }
        return Range(x.coerceAtLeast(other.x),
                right().coerceAtMost(other.right())
        )
    }