package xyz.jmullin.ld38.entity

import xyz.jmullin.drifter.animation.Trigger
import xyz.jmullin.drifter.animation.delay
import xyz.jmullin.drifter.entity.Entity2D
import xyz.jmullin.drifter.entity.EntityContainer2D
import xyz.jmullin.drifter.extensions.rFloat

class TrafficController : Entity2D() {
    fun nextShip(time: Float): Trigger = delay(time) {
        parent?.add(Traffic())
        nextShip(rFloat(20f, 30f)) go(this)
    }

    override fun create(container: EntityContainer2D) {
        nextShip(0.5f) go(this)

        super.create(container)
    }
}