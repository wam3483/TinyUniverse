package com.pixelatedmind.game.tinyuniverse.ui.model

import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.util.*

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
open class NameableId(var id : String, var name: String){
    constructor() : this(UUID.randomUUID().toString(),"") {
    }

    override fun equals(other: Any?): Boolean {
        if(other==null || !(other is NameableId)) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result *= 31
        return result
    }
}