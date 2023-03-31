package com.pixelatedmind.game.tinyuniverse.ui

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.kotcrab.vis.ui.widget.tabbedpane.Tab

class SimpleTab(var title : String, var content : Table, saveable : Boolean = false, closeableByUser : Boolean = false)
    : Tab(saveable, closeableByUser) {
    override fun getTabTitle(): String {
        return title
    }

    override fun getContentTable(): Table {
        return content
    }
}