package xyz.jmullin.ld38.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import xyz.jmullin.drifter.entity.Entity2D
import xyz.jmullin.drifter.entity.EntityContainer2D
import xyz.jmullin.drifter.extensions.*
import xyz.jmullin.drifter.extensions.FloatMath.cos
import xyz.jmullin.drifter.extensions.FloatMath.sin
import xyz.jmullin.drifter.sound.Play
import xyz.jmullin.ld38.Assets

open class Button(val definition: ButtonDefinition) : Entity2D() {

    var warn: Float = 0f
    val baseColor: Color get() = definition.color.cpy().lerp(C(0.6f, 0.6f, 0.5f), 0.5f-cos(warn*3f)*0.5f)

    val highlightColor: Color get() = baseColor + 0.1f + 0.3f*(1f-raisedAmount/maxRaisedAmount)
    val shadowColor: Color get() = baseColor - 0.1f + 0.3f*(1f-raisedAmount/maxRaisedAmount)
    val textColor: Color get() = highlightColor + 0.5f + 0.3f*(1f-raisedAmount/maxRaisedAmount)
    val buttonColor: Color get() = baseColor + 0.3f*(1f-raisedAmount/maxRaisedAmount)

    open val maxRaisedAmount = 7f
    open val depressDelay = 5f
    open var raisedAmount = 7f

    override fun create(container: EntityContainer2D) {
        definition.parent = this

        super.create(container)
    }

    override fun update(delta: Float) {
        if(definition.toggle) {
            if(definition.toggled) {
                raisedAmount -= raisedAmount / depressDelay
                definition.hold(delta)
            } else {
                raisedAmount += (maxRaisedAmount - raisedAmount) / depressDelay
            }
        } else {
            if(!Gdx.input.isTouched || !containsPoint(mouseV())) {
                if(raisedAmount == 0f) {
                    definition.depressed()
                }
                raisedAmount += (maxRaisedAmount - raisedAmount) / depressDelay
            } else {
                definition.hold(delta)
            }
        }

        if(Gdx.input.justTouched() && containsPoint(mouseV())) {
            if(definition.toggle) {
                definition.toggled = !definition.toggled
                if(definition.toggled) {
                    definition.pressed()
                } else {
                    definition.depressed()
                }
            } else {
                definition.pressed()
                raisedAmount = 0f
            }
            Play.sound(Assets.randomType(), 0.4f..0.5f, 1.2f..1.4f)
        }

        super.update(delta)
    }
}