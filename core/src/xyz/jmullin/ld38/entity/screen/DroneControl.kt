package xyz.jmullin.ld38.entity.screen

import com.badlogic.gdx.graphics.Color
import xyz.jmullin.drifter.entity.EntityContainer2D
import xyz.jmullin.drifter.extensions.*
import xyz.jmullin.drifter.extensions.FloatMath.clamp
import xyz.jmullin.drifter.extensions.FloatMath.max
import xyz.jmullin.drifter.extensions.FloatMath.min
import xyz.jmullin.drifter.rendering.RenderStage
import xyz.jmullin.drifter.rendering.sprite
import xyz.jmullin.drifter.rendering.string
import xyz.jmullin.drifter.sound.Play
import xyz.jmullin.ld38.Assets
import xyz.jmullin.ld38.Stage.DrawColor
import xyz.jmullin.ld38.entity.*

object DroneControl : Screen() {
    val reticleV = V2(0f)
    val ReticleSpeed = 2f

    var tick = 0f

    override fun create(container: EntityContainer2D) {
        reticleV.set(screenSize/2f)

        super.create(container)
    }

    override fun render(stage: RenderStage) {
        DrawColor.draw(stage) {
            if(!Drone.launched) {
                Assets.prototype14.color = Color.WHITE
                string("Drone is in bay storage.", origin + screenSize/2f, Assets.prototype14, V2(0f))
            } else if(Scan.structuralScan < 0.05f) {
                Assets.prototype14.color = Color.WHITE
                string("Targeting overlay requires sensor calibration.", origin + screenSize/2f, Assets.prototype14, V2(0f))
            } else {
                Assets.droneTargeting.color = C(Color.WHITE, max(Scan.structuralScan, Drone.active))
                sprite(Assets.droneTargeting, origin - V2(0f, 12f), screenSize)
                Scan.structuralOnly = true
                Scan.offset.set(shipEntity.position - Drone.position)
                Scan.render(stage)
                Scan.offset.set(V2(0f))
                Scan.structuralOnly = false

                if(Drone.repairBeamOn || Drone.deconBeamOn) {
                    Assets.circle.color = C(Color.WHITE, min(2f - (tick % 2f), 1f))
                    val circleSize = V2((tick % 2f) * 40f)
                    sprite(Assets.circle, origin + screenSize/2f - circleSize/2f, circleSize)
                }

                sprite(Assets.drone.frame(0.5f), origin + screenSize/2f - Assets.drone.frame(0.5f).size/2f, Assets.drone.frame(0.5f).size)

                sprite(Assets.reticle, origin + reticleV-Assets.reticle.size/2f, Assets.reticle.size)

                Assets.prototype14.color = C(Color.WHITE, Drone.decontaminating)
                string("Decontaminating...", V2(35f, screenSize.y - 8f), Assets.prototype14, V2(1f, -1f))
                Assets.prototype14.color = C(Color.WHITE, Drone.repairing)
                string("Repairing...", V2(screenSize.x-35f, screenSize.y - 8f), Assets.prototype14, V2(-1f, -1f))
            }
        }
    }

    override fun update(delta: Float) {
        tick += delta

        super.update(delta)
    }

    fun clampReticle() {
        reticleV.set(clamp(reticleV.x, 25f, screenSize.x-25f), clamp(reticleV.y, 25f, screenSize.y-25f))
    }

    fun target() = (reticleV - screenSize/2f) + Drone.position

    override val buttons = listOf(
        Recall, UpButton, Launch,
        LeftButton, MoveButton, RightButton,
        Decontaminate, DownButton, Repair
    )
}

val Launch = press("Launch") {
    if(!Drone.launched) {
        Drone.launched = true
        Drone.moveTo(rV(V2(412f, 400f), V2(612f, 450f)), 3f)
    } else {
        Errors.log("Drone has already been launched.")
    }
}

val Recall = press("Recall") {
    if(Drone.launched) {
        Drone.launched = false
        Drone.moveTo(V2(512f, 275f), 3f)
    } else {
        Errors.log("Drone is not currently launched.")
    }
}

val UpButton = hold("Up") { DroneControl.reticleV.add(V2(0f, DroneControl.ReticleSpeed)); DroneControl.clampReticle() }
val DownButton = hold("Down") { DroneControl.reticleV.add(V2(0f, -DroneControl.ReticleSpeed)); DroneControl.clampReticle() }
val LeftButton = hold("Left") { DroneControl.reticleV.add(V2(-DroneControl.ReticleSpeed, 0f)); DroneControl.clampReticle() }
val RightButton = hold("Right") { DroneControl.reticleV.add(V2(DroneControl.ReticleSpeed, 0f)); DroneControl.clampReticle() }

val MoveButton = press("Move") {
    if(Drone.launched) {
        Play.sound(Assets.accepted, 0.6f..0.8f, 0.9f..1f)
        Drone.moveTo(DroneControl.target())
    } else {
        Errors.log("Drone is not currently launched.")
    }
}

val Decontaminate: ButtonDefinition = object : ButtonDefinition("Decontam.", true) {
    override fun pressed() {
        Drone.deconBeamOn = true
        super.pressed()
    }

    override fun depressed() {
        Drone.deconBeamOn = false
    }
}

val Repair = object : ButtonDefinition("Repair", true) {
    override fun pressed() {
        Drone.repairBeamOn = true
        super.pressed()
    }

    override fun depressed() {
        Drone.repairBeamOn = false
    }
}