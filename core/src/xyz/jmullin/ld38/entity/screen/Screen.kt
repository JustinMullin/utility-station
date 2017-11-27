package xyz.jmullin.ld38.entity.screen

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import xyz.jmullin.drifter.entity.Entity2D
import xyz.jmullin.drifter.extensions.V2
import xyz.jmullin.drifter.extensions.div
import xyz.jmullin.drifter.extensions.plus
import xyz.jmullin.drifter.rendering.string
import xyz.jmullin.ld38.Assets
import xyz.jmullin.ld38.entity.Panel
import xyz.jmullin.ld38.entity.RadioValue
import xyz.jmullin.ld38.entity.disabled

open class Screen() : Entity2D() {
    fun active(): Boolean = activeLeft() == this || activeRight() == this

    fun drawName(batch: SpriteBatch, name: String) {
        batch.string(name, origin + screenSize/2f, Assets.steelfish28, V2(0f))
    }

    var notification = false
    var origin = V2(0f)
    var screenSize = V2(0f)

    fun notification() {
        notification = true
    }

    open val buttons = listOf(
        disabled(), disabled(), disabled(),
        disabled(), disabled(), disabled(),
        disabled(), disabled(), disabled()
    )
}

val screens = listOf(
    Comms, DroneControl, Scan, Performance,
    Errors, Provisions, Navigation, Fuel)

val leftScreenSelection = RadioValue(3)
val rightScreenSelection = RadioValue()

fun activeLeft() = screens.getOrNull(leftScreenSelection.value)
fun activeRight() = screens.getOrNull(rightScreenSelection.value + 4)