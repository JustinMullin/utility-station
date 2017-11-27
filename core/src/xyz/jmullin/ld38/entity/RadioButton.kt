package xyz.jmullin.ld38.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import xyz.jmullin.drifter.entity.Entity2D
import xyz.jmullin.drifter.extensions.minus
import xyz.jmullin.drifter.extensions.mouseV
import xyz.jmullin.drifter.extensions.plus
import xyz.jmullin.drifter.extensions.rElement
import xyz.jmullin.drifter.sound.Play
import xyz.jmullin.ld38.Assets

open class RadioButton(val id: Int, definition: ButtonDefinition, val radioValue: RadioValue) : Button(definition) {

    final override val maxRaisedAmount: Float = 4f
    override var raisedAmount: Float = maxRaisedAmount

    val pressDelay = 2f

    override fun update(delta: Float) {
        val target = if(id == radioValue.value) 0f else maxRaisedAmount
        val delay = if(id == radioValue.value) pressDelay else depressDelay
        raisedAmount += (target - raisedAmount) / delay

        if(Gdx.input.justTouched() && containsPoint(mouseV())) {
            if(radioValue.value != id) {
                Play.sound(Assets.randomType(), 0.4f..0.5f, 1.2f..1.4f)
            }
            radioValue.value = id
        }
    }
}