package xyz.jmullin.ld38.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import xyz.jmullin.drifter.entity.Entity2D
import xyz.jmullin.drifter.extensions.*
import xyz.jmullin.drifter.rendering.Draw
import xyz.jmullin.drifter.rendering.RenderStage
import xyz.jmullin.drifter.rendering.sprite
import xyz.jmullin.ld38.Stage.DrawColor

class Window : Entity2D() {
    override fun render(stage: RenderStage) {
        DrawColor.draw(stage) {
            boxOutline(position, size, Color.GREEN, 4f, this)
        }

        super.render(stage)
    }

    fun boxOutline(v: Vector2, size: Vector2, color: Color, margin: Float, batch: SpriteBatch) {
        Draw.fill.color = color
        batch.sprite(Draw.fill, v, V2(margin, size.y))
        batch.sprite(Draw.fill, v + V2(size.x - margin, 0f), V2(margin, size.y))
        batch.sprite(Draw.fill, v + V2(margin, 0f), V2(size.x-margin*2f, margin))
        batch.sprite(Draw.fill, v + V2(margin, size.y - margin), V2(size.x-margin*2f, margin))
    }
}