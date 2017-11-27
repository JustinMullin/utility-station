package xyz.jmullin.ld38.entity

import xyz.jmullin.drifter.entity.Entity2D
import xyz.jmullin.drifter.extensions.*
import xyz.jmullin.drifter.rendering.RenderStage
import xyz.jmullin.drifter.rendering.texture
import xyz.jmullin.ld38.Assets
import xyz.jmullin.ld38.Stage.DrawColor

class Background() : Entity2D() {
    init {
        depth = 100
    }

    override fun render(stage: RenderStage) {
        DrawColor.draw(stage) {
            texture(Assets.stars, V2(0f), gameSize())
        }

        super.render(stage)
    }
}