package xyz.jmullin.ld38.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import xyz.jmullin.drifter.extensions.*
import xyz.jmullin.drifter.extensions.FloatMath.max
import xyz.jmullin.drifter.extensions.FloatMath.min
import xyz.jmullin.drifter.rendering.Draw
import xyz.jmullin.drifter.rendering.RenderStage
import xyz.jmullin.drifter.rendering.quad
import xyz.jmullin.drifter.rendering.string
import xyz.jmullin.ld38.Assets
import xyz.jmullin.ld38.Stage.DrawColor

class ConsoleButton(definition: ButtonDefinition, left: Vector2, right: Vector2, val bufferSize: Vector2, val region: TextureRegion, buttonHeight: Float) : Button(definition) {
    companion object {
        val vanishingPoint = V2(gameW()/2f, 1000f)
    }

    init {
        left.add(definition.offset)
        right.add(definition.offset)
    }

    val labelSprite = Sprite(region)

    val leftNormal: Vector2 = (vanishingPoint - left).nor()
    val rightNormal: Vector2 = (vanishingPoint - right).nor()

    val a = left
    val b = left + V2((leftNormal * buttonHeight).x, buttonHeight)
    val c = right + V2((rightNormal * buttonHeight).x, buttonHeight)
    val d = right

    val leftCenter = (a+b)/2f
    val rightCenter = (c+d)/2f
    val center = (leftCenter+rightCenter)/2f

    override val bounds: Rectangle
        get() = Rect(min(a.x, b.x), a.y, max(c.x, d.x)-min(a.x, b.x), b.y-a.y+maxRaisedAmount)

    fun drawLabel(stage: RenderStage) {
        DrawColor.draw(stage) {
            Assets.prototype14.color = textColor
            string(definition.label, V2((region.u + region.u2)/2f, (region.v + region.v2)/2f) * bufferSize, Assets.prototype14, V2(0f, 0f))
        }
    }

    override fun render(stage: RenderStage) {
        DrawColor.draw(stage) {
            quad(Draw.fill, a+V2(0f, raisedAmount), b+V2(0f, raisedAmount), c+V2(0f, raisedAmount), d+V2(0f, raisedAmount), buttonColor)
            quad(labelSprite, a+V2(0f, raisedAmount), b+V2(0f, raisedAmount), c+V2(0f, raisedAmount), d+V2(0f, raisedAmount), Color.WHITE)
            quad(Draw.fill, a, a+V2(0f, raisedAmount), d+V2(0f, raisedAmount), d, shadowColor)
            if(c.x > d.x) quad(Draw.fill, d, d+V2(0f, raisedAmount), c+V2(0f, raisedAmount), c, highlightColor)
            if(b.x < a.x) quad(Draw.fill, a, a+V2(0f, raisedAmount), b+V2(0f, raisedAmount), b, highlightColor)
        }

        super.render(stage)
    }
}