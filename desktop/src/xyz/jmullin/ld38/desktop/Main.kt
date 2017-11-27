package xyz.jmullin.ld38.desktop

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.graphics.profiling.GLProfiler
import xyz.jmullin.drifter.extensions.GdxAlias
import xyz.jmullin.drifter.extensions.V2
import xyz.jmullin.ld38.LD38

fun main(args: Array<String>): Unit {
    val config = LwjglApplicationConfiguration()

    if(args.contains("dev")) {
        LD38.devMode = true
    }

    config.title = LD38.name
//    config.x = 0
    config.width = 1024
    config.height = 768
//    config.width = LwjglApplicationConfiguration.getDesktopDisplayMode().width
//    config.height = LwjglApplicationConfiguration.getDesktopDisplayMode().height
//    config.fullscreen = true
    config.resizable = false

    config.vSyncEnabled = true
    config.foregroundFPS = 60
//    config.samples = 4

    GdxAlias.fixGameSize(V2(1024f, 768))

    LwjglApplication(LD38, config)
}