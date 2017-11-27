package xyz.jmullin.ld38.entity

import com.badlogic.gdx.graphics.Color
import xyz.jmullin.drifter.entity.Entity2D
import xyz.jmullin.drifter.entity.EntityContainer2D
import xyz.jmullin.drifter.extensions.*
import xyz.jmullin.drifter.extensions.FloatMath.abs
import xyz.jmullin.drifter.rendering.RenderStage
import xyz.jmullin.drifter.rendering.sprite
import xyz.jmullin.drifter.sound.Play
import xyz.jmullin.ld38.Assets
import xyz.jmullin.ld38.Stage.DrawColor

object DockingArm : Entity2D() {
    init {
        depth = 8
    }

    val TractorAccel = 0.01f
    val ArmAccel = 0.02f
    val armLength = 375f

    var armLocked = false
    var armRetractTime = 0f
    var grab = 0f
    var armPosition = 0f
    var tractorOn = false
    var repulsorOn = false
    var armExtending = false
    var armRetracting = false
    var beamWidth = 0f
    var tractorSpeed = 0f
    var armSpeed = 0f

    var soundId = 0L
    var armSoundId = 0L

    override fun create(container: EntityContainer2D) {
        soundId = Assets.tractorLoop.loop(0f)
        armSoundId = Assets.armLoop.loop(0f)

        super.create(container)
    }

    override fun render(stage: RenderStage) {
        DrawColor.draw(stage) {
            if(repulsorOn) {
                Assets.tractorBeam.color = C(Color.YELLOW, rFloat(0.7f, 1f))
            } else if(tractorOn) {
                Assets.tractorBeam.color = C(Color.CYAN, rFloat(0.7f, 1f))
            } else {
                Assets.tractorBeam.color = C(Color.WHITE, rFloat(0.7f, 1f))
            }

            sprite(Assets.tractorBeam, V2(gameW()/2f-175f*beamWidth, 100f), V2(350f * beamWidth, 400f))
            sprite(Assets.grabber.frame(grab), V2(gameW()/2f-30f, armPosition), V2(60f, 383f))
        }
    }

    override fun update(delta: Float) {
        Assets.tractorLoop.setVolume(soundId, beamWidth*0.5f)

        if(repulsorOn || tractorOn) {
            beamWidth += (1f - beamWidth) / 70f
        } else {
            beamWidth -= beamWidth / 20f
            beamWidth -= 0.01f
            if(beamWidth < 0f) beamWidth = 0f
        }

        if(armLocked) {
            grab += (1f-grab) / 10f
        } else {
            grab += (0f-grab) / 10f
        }

        if(repulsorOn && abs(shipEntity.x - gameW()/2f) < 30f) {
            tractorSpeed += TractorAccel
            if(tractorSpeed >= 0.3f) tractorSpeed = 0.3f
        } else if(tractorOn && abs(shipEntity.x - gameW()/2f) < 30f) {
            tractorSpeed -= TractorAccel
            if(tractorSpeed <= -0.3f) tractorSpeed = -0.3f
        } else {
            tractorSpeed *= 0.99f
        }

        shipEntity.position.add(V2(0f, tractorSpeed))
        shipEntity.position.set(shipEntity.position.x, FloatMath.clamp(shipEntity.position.y, gameH()/2f+50f, gameH()-50f))

        Assets.armLoop.setVolume(armSoundId, abs(armSpeed)*2f)

        if(armRetracting || armRetractTime > 0f) {
            Assets.armLoop.setPitch(armSoundId, 1.02f)
            armRetractTime -= delta
            if(armRetractTime < 0f) armRetractTime = 0f
            armSpeed -= ArmAccel
            if(armSpeed <= -0.3f) armSpeed = -0.3f
        } else if(armExtending) {
            Assets.armLoop.setPitch(armSoundId, 0.98f)
            armSpeed += ArmAccel
            if(armSpeed >= 0.3f) armSpeed = 0.3f
        } else {
            armSpeed *= 0.98f
        }

        armPosition += armSpeed
        if(armPosition <= 0f) {
            armSpeed = 0f
            armPosition = 0f
            armRetracting = false
            Retract.toggled = false
        }
        if(armPosition >= 220f) {
            armSpeed = 0f
            armPosition = 220f
            armExtending = false
            Extend.toggled = false
        }

        if(armLocked) {
            shipEntity.position.add(0f, (armPosition + armLength - shipEntity.position.y + shipEntity.spriteSize.y/4f) / 15f)
        }

        super.update(delta)
    }
}