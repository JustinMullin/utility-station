package xyz.jmullin.ld38.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import xyz.jmullin.drifter.extensions.C
import xyz.jmullin.drifter.extensions.V2
import xyz.jmullin.ld38.entity.screen.leftScreenSelection
import xyz.jmullin.ld38.entity.screen.rightScreenSelection
import xyz.jmullin.ld38.entity.screen.screens

open class ButtonDefinition(private val _label: String, private val _toggle: Boolean = false, private val _color: Color = C(0.2f, 0.3f, 0.4f), val offset: Vector2 = V2(0f)) {
    var parent: Button? = null

    var _toggled = false

    open val label: String get() = _label
    open val toggle: Boolean get() = _toggle
    open val color: Color get() = _color
    open var toggled: Boolean
        get() = _toggled
        set(t) { _toggled = t }

    open fun pressed() {}
    open fun depressed() {}
    open fun hold(delta: Float) {}

    fun pressParent() {
        parent?.raisedAmount = 0f
    }
}

val mainButtons = listOf(
    screen(true, 6), screen(true, 7), screen(true, 8),
    disabled(), disabled(), disabled(),
    screen(false, 6), screen(false, 7), screen(false, 8),

    screen(true, 3), screen(true, 4), screen(true, 5),
    Tractor, Release, Retract,
    screen(false, 3), screen(false, 4), screen(false, 5),

    screen(true, 0), screen(true, 1), screen(true, 2),
    Repulsor, Lock, Extend,
    screen(false, 0), screen(false, 1), screen(false, 2)
    )

val leftRadioButtons = listOf(
    press("Comms"), press("Drone"), press("Scan"), press("Central")
)

val rightRadioButtons = listOf(
    press("Error"), press("Provisions"), press("Navigation"), press("Fuel")
)

fun press(label: String, onPress: () -> Unit = {}) = object : ButtonDefinition(label) {
    override fun pressed() {
        onPress()
    }
}

fun toggle(label: String, onPress: () -> Unit = {}) = object : ButtonDefinition(label, true) {
    override fun pressed() {
        onPress()
    }
}

fun hold(label: String, onHold: () -> Unit = {}) = object : ButtonDefinition(label) {
    override fun hold(delta: Float) {
        onHold()
    }
}

fun disabled() = object : ButtonDefinition("", false, C(0.1f)) {}
fun screen(left: Boolean, id: Int) = object : ButtonDefinition("", false) {
    fun parent() = screens.getOrNull((if(left) leftScreenSelection else rightScreenSelection).value + (if(left) 0 else 4))
    fun currentButton() = parent()?.buttons?.getOrNull(id) ?: disabled()

    override val label: String
        get() = currentButton().label

    override val toggle: Boolean
        get() = currentButton().toggle

    override var toggled: Boolean
        get() = currentButton().toggled
        set(value) { currentButton().toggled = value }

    override val color: Color
        get() = currentButton().color

    override fun pressed() {
        currentButton().pressed()
    }

    override fun depressed() {
        currentButton().depressed()
    }

    override fun hold(delta: Float) {
        currentButton().hold(delta)
    }
}