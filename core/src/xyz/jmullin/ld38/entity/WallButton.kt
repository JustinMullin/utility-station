package xyz.jmullin.ld38.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import xyz.jmullin.drifter.entity.Entity2D
import xyz.jmullin.drifter.extensions.*
import xyz.jmullin.drifter.extensions.FloatMath.max
import xyz.jmullin.drifter.extensions.FloatMath.min
import xyz.jmullin.drifter.rendering.*
import xyz.jmullin.ld38.Assets
import xyz.jmullin.ld38.Stage.DrawColor
import xyz.jmullin.ld38.entity.screen.screens

class WallButton(id: Int, val globalId: Int, definition: ButtonDefinition, radioValue: RadioValue, val v: Vector2, val buttonSize: Vector2) : RadioButton(id, definition, radioValue) {
    val center = v + buttonSize/2f

    val onRight = v.x > gameW()/2f

    val backOffset = V2(if(onRight) -3f else 3f, 5f)

    override val bounds: Rectangle
        get() = Rect(v, buttonSize)

    override fun render(stage: RenderStage) {
        val pressAlpha = raisedAmount/maxRaisedAmount
        val pressOffset = backOffset*pressAlpha

        DrawColor.draw(stage) {
            quad(Draw.fill,
                v + V2(0f, buttonSize.y), v + V2(0f, buttonSize.y) + pressOffset,
                v + buttonSize + pressOffset, v + buttonSize, shadowColor)

            if(onRight) {
                quad(Draw.fill,
                    v + pressOffset, v + V2(0f, buttonSize.y) + pressOffset,
                    v + V2(0f, buttonSize.y), v, highlightColor)
            } else {
                quad(Draw.fill,
                    v + V2(buttonSize.x, 0f), v + buttonSize,
                    v + buttonSize + pressOffset, v + V2(buttonSize.x, 0f) + pressOffset, highlightColor)
            }

//            Draw.fill.color = buttonColor-0.04f
//            sprite(Draw.fill, v + backOffset*(1f-pressAlpha)+(1f-pressAlpha)*2f, buttonSize-(1f-pressAlpha)*2f)
//            Draw.fill.color = buttonColor+0.04f
//            sprite(Draw.fill, v + backOffset*(1f-pressAlpha)-(1f-pressAlpha)*2f, buttonSize-(1f-pressAlpha)*2f)
            Draw.fill.color = buttonColor
            sprite(Draw.fill, v + backOffset*(1f-pressAlpha), buttonSize)

            Assets.prototype14.color = shadowColor
            V2(0f).orthogonal().map {
                if(onRight) {
                    string(definition.label, center - V2(buttonSize.x-3f, 0f), Assets.prototype14, V2(-1f, 0f))
                } else {
                    string(definition.label, center + V2(buttonSize.x-3f, 0f), Assets.prototype14, V2(1f, 0f))
                }
            }

            Assets.prototype14.color = textColor
            if(onRight) {
                string(definition.label, center - V2(buttonSize.x-3f, 0f), Assets.prototype14, V2(-1f, 0f))
            } else {
                string(definition.label, center + V2(buttonSize.x-3f, 0f), Assets.prototype14, V2(1f, 0f))
            }
        }

        super.render(stage)
    }

    override fun update(delta: Float) {
        if(screens[globalId].notification) {
            warn += delta
        } else {
            warn = 0f
        }

        super.update(delta)
    }
}