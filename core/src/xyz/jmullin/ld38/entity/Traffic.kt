package xyz.jmullin.ld38.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import xyz.jmullin.drifter.animation.tween
import xyz.jmullin.drifter.entity.Entity2D
import xyz.jmullin.drifter.entity.EntityContainer2D
import xyz.jmullin.drifter.extensions.*
import xyz.jmullin.drifter.rendering.RenderStage
import xyz.jmullin.drifter.rendering.sprite
import xyz.jmullin.ld38.Assets
import xyz.jmullin.ld38.Stage.DrawColor

/**
 * @author Justin Mullin
 */
class Traffic : Entity2D() {
    val flipped = probability(0.5f)

    init {
        depth = rInt(0, 20)
    }

    val scale = rFloat(0.5f, 0.6f)
    val shipSprite = Sprite(rElement(listOf(Assets.ship1, Assets.ship2, Assets.ship3)))
    val spriteSize = shipSprite.size.cpy() * scale
    val origin = if(flipped) {
        V2(-spriteSize.x/2f, rFloat(gameH()/2f, gameH() - spriteSize.y/2f))
    } else {
        V2(gameW() + spriteSize.x/2f, rFloat(gameH()/2f, gameH() - spriteSize.y/2f))
    }

    override val bounds: Rectangle
        get() = Rect(position - spriteSize/2f-10f, spriteSize + 20f)

    override fun create(container: EntityContainer2D) {
        position.set(origin)
        tween(rFloat(50f, 70f)) { n ->
            if(flipped) {
                position.set(origin + V2(n * (gameW() + spriteSize.x), 0f))
            } else {
                position.set(origin - V2(n * (gameW() + spriteSize.x), 0f))
            }
        } then {
            remove()
        } go(this)

        super.create(container)
    }

    override fun render(stage: RenderStage) {
        DrawColor.draw(stage) {
            if(flipped) {
                sprite(shipSprite, position - V2(-1f, 1f)*spriteSize/2f, spriteSize * V2(-1f, 1f))
            } else {
                sprite(shipSprite, position - spriteSize/2f, spriteSize)
            }
        }

        super.render(stage)
    }

    override fun update(delta: Float) {
        if(Gdx.input.justTouched() && containsPoint(mouseV())) {
            /*val window = Window()
            window.position.set(V2(bounds.x, bounds.y))
            window.size.set(V2(bounds.width, bounds.height))
            parent?.add(window)*/
        }

        super.update(delta)
    }
}