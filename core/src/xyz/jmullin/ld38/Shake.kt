package xyz.jmullin.ld38

import xyz.jmullin.drifter.entity.Entity2D
import xyz.jmullin.drifter.entity.EntityContainer2D
import xyz.jmullin.ld38.Shake.amount

object Shake {
    var amount: Float = 0f
    var instance: ShakeController? = null

    fun shake(intensity: Float, falloff: Float=0.25f) {
        instance?.let { i ->
            i.events += ShakeEvent(intensity, falloff)
        }
    }
}

class ShakeController : Entity2D() {
    var events = listOf<ShakeEvent>()

    override fun create(container: EntityContainer2D) {
        Shake.instance = this

        super.create(container)
    }

    override fun update(delta: Float) {
        events = events.mapNotNull { it.reduce() }
        amount = events.maxBy { it.intensity }?.intensity ?: 0f

        super.update(delta)
    }
}

data class ShakeEvent(val intensity: Float, val falloff: Float) {
    fun reduce(): ShakeEvent? {
        return if(intensity > 0.01f) {
            ShakeEvent(intensity * 1f-falloff, falloff)
        } else {
            null
        }
    }
}