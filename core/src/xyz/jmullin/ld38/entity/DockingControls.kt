package xyz.jmullin.ld38.entity

import xyz.jmullin.drifter.animation.tween
import xyz.jmullin.drifter.extensions.FloatMath.abs
import xyz.jmullin.drifter.extensions.gameW
import xyz.jmullin.drifter.sound.Play
import xyz.jmullin.ld38.Assets
import xyz.jmullin.ld38.entity.DockingArm.armLength
import xyz.jmullin.ld38.entity.DockingArm.armLocked
import xyz.jmullin.ld38.entity.DockingArm.armPosition
import xyz.jmullin.ld38.entity.DockingArm.armRetractTime
import xyz.jmullin.ld38.entity.DockingArm.armSpeed
import xyz.jmullin.ld38.entity.screen.Errors

val Retract: ButtonDefinition = object : ButtonDefinition("Retract", true) {
    override fun pressed() {
        if(armLocked) {
            Errors.log("Docking arm is locked.")
            toggled = false
            pressParent()
        } else {
            Extend.toggled = false
            DockingArm.armExtending = false
            DockingArm.armRetracting = true
        }
        super.pressed()
    }

    override fun depressed() {
        DockingArm.armRetracting = false
        super.depressed()
    }
}

val Extend = object : ButtonDefinition("Extend", true) {
    override fun pressed() {
        if(armLocked) {
            Errors.log("Docking arm is locked.")
            toggled = false
            pressParent()
        } else {
            Retract.toggled = false
            DockingArm.armExtending = true
            DockingArm.armRetracting = false
        }
        super.pressed()
    }

    override fun depressed() {
        DockingArm.armExtending = false
        super.depressed()
    }
}

val Repulsor: ButtonDefinition = object : ButtonDefinition("Repulsor", true) {
    override fun pressed() {
        Tractor.toggled = false
        DockingArm.tractorOn = false
        DockingArm.repulsorOn = true
        super.pressed()
    }

    override fun depressed() {
        DockingArm.repulsorOn = false
        super.depressed()
    }
}

val Tractor = object : ButtonDefinition("Tractor", true) {
    override fun pressed() {
        Repulsor.toggled = false
        DockingArm.tractorOn = true
        DockingArm.repulsorOn = false
        super.pressed()
    }

    override fun depressed() {
        DockingArm.tractorOn = false
        super.depressed()
    }
}

val Lock = object : ButtonDefinition("Lock") {
    override fun pressed() {
        if(shipEntity.leaving) {
            Errors.log("Spacecraft is en route to depart.")
        } else if(abs(armSpeed) > 0.1f) {
            Errors.log("Docking arm is in motion.")
        } else if(abs(armPosition + armLength - shipEntity.y + shipEntity.spriteSize.y/4f) > 20f || abs(shipEntity.x - gameW()/2f) > 20f) {
            Errors.log("Docking arm is not in position.")
        } else if(armLocked) {
            Errors.log("Docking arm already locked.")
        } else {
            Play.sound(Assets.lock, 0.1f..0.2f, 0.9f..1f)
            ship.docked = true
            armLocked = true
        }
    }
}

val Release = object : ButtonDefinition("Release") {
    override fun pressed() {
        if(!armLocked) {
            Errors.log("Docking arm not currently locked.")
        } else {
            Play.sound(Assets.lock, 0.1f..0.2f, 1.4f..1.5f)
            armLocked = false
            armRetractTime = 1f
        }
    }
}