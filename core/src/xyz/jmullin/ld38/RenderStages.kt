package xyz.jmullin.ld38

import com.badlogic.gdx.graphics.Pixmap
import xyz.jmullin.drifter.extensions.C
import xyz.jmullin.drifter.extensions.gameSize
import xyz.jmullin.drifter.rendering.BlitStage
import xyz.jmullin.drifter.rendering.BufferStage
import xyz.jmullin.drifter.rendering.DrawStage
import xyz.jmullin.drifter.rendering.shader

object Stage {
    private val PostprocessingShader = shader("postprocessing", "default") { program ->
        program.setUniformf("resolution", gameSize())
    }

    val DrawColor by lazy { BufferStage("colorBuffer", Pixmap.Format.RGBA8888, C(0f), gameSize()) }
    val Postprocessing by lazy { BlitStage("postprocessing", listOf(DrawColor), PostprocessingShader) }

    val Ui by lazy { DrawStage("draw") }
}