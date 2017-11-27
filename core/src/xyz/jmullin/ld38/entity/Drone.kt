package xyz.jmullin.ld38.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import xyz.jmullin.drifter.animation.Easing
import xyz.jmullin.drifter.animation.tween
import xyz.jmullin.drifter.entity.Entity2D
import xyz.jmullin.drifter.entity.EntityContainer2D
import xyz.jmullin.drifter.extensions.FloatMath.cos
import xyz.jmullin.drifter.extensions.FloatMath.sin
import xyz.jmullin.drifter.extensions.*
import xyz.jmullin.drifter.rendering.RenderStage
import xyz.jmullin.drifter.rendering.sprite
import xyz.jmullin.ld38.Assets
import xyz.jmullin.ld38.Stage.DrawColor
import xyz.jmullin.ld38.entity.screen.DroneControl

object Drone : Entity2D() {
    var tick = 0f
    var beam = 0f

    var deconBeamOn = false
    var repairBeamOn = false
    var launched = false

    var decontaminating = 0f
    var repairing = 0f
    var active = 0f

    init {
        depth = 9
    }

    fun moveTo(v: Vector2, overrideDuration: Float? = null) {
        clearHooks()
        val startPosition = position.cpy()
        val path = v - position
        tween(overrideDuration ?: (path.len() / 20f), Easing.Companion.Sine) { n ->
            position.set(startPosition + path * n)
        } go(this)
    }

    override fun create(container: EntityContainer2D) {
        position.set(512f, 275f)

        super.create(container)
    }

    override fun render(stage: RenderStage) {
        val beamColor =
            if(repairBeamOn && deconBeamOn) {
                Color.WHITE
            } else if(repairBeamOn) {
                Color.YELLOW
            } else if(deconBeamOn) {
                Color.RED
            } else {
                Color.WHITE
            }

        DrawColor.draw(stage) {
            val frame = Assets.drone.frame(0.5f+cos(tick * 5f)*0.5f)

            val wobble = V2(cos(tick*2.1f)*2f, sin(tick*1.3f)*2f)

            Assets.droneBeam.color = C(beamColor, rFloat(0.9f, 1f)*beam)
            sprite(Assets.droneBeam, position - V2(8f*(0.5f+beam*0.5f), 25f) + wobble, V2(16f*(0.5f+beam*0.5f), 20f))

            sprite(frame, position - frame.size/2f + wobble, frame.size)
        }

        super.render(stage)
    }

    override fun update(delta: Float) {
        tick += delta

        decontaminating *= 0.9f
        repairing *= 0.9f

        if(launched) active += (1f - active) / 10f
        else active += (0f - active) / 10f

        if(deconBeamOn) {
            ship.contaminations.forEach { con ->
                val absoluteV = shipEntity.position - shipEntity.spriteSize/2f + con.v
                if((absoluteV - position).len() < 40f) {
                    con.strength -= delta
                    decontaminating = 1f
                    if(con.strength <= 0f) {
                        ship.contaminations = ship.contaminations.filterNot { it.v == con.v }
                    }
                }
            }
        }

        if(repairBeamOn) {
            ship.damages.forEach { con ->
                val absoluteV = shipEntity.position - shipEntity.spriteSize/2f + con.v
                if((absoluteV - position).len() < 40f) {
                    con.strength -= delta
                    repairing = 1f
                    if(con.strength <= 0f) {
                        ship.damages = ship.damages.filterNot { it.v == con.v }
                    }
                }
            }
        }

        if((repairBeamOn || deconBeamOn) && hooks.isEmpty()) {
            beam += (1f-beam)/10f
        } else {
            beam -= beam / 10f
        }

        super.update(delta)
    }
}