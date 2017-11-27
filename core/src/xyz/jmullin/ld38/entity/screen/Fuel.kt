package xyz.jmullin.ld38.entity.screen

import com.badlogic.gdx.graphics.Color
import xyz.jmullin.drifter.entity.EntityContainer2D
import xyz.jmullin.drifter.extensions.*
import xyz.jmullin.drifter.rendering.Draw
import xyz.jmullin.drifter.rendering.RenderStage
import xyz.jmullin.drifter.rendering.sprite
import xyz.jmullin.drifter.rendering.string
import xyz.jmullin.ld38.Assets
import xyz.jmullin.ld38.Stage.DrawColor
import xyz.jmullin.ld38.entity.*
import xyz.jmullin.ld38.entity.DockingArm.armLocked

object Fuel : Screen() {
    val tankNames = "ABCD".toList()
    val tankColors = listOf(C(1f, 0.6f, 0.6f), C(0.6f, 1f, 0.6f), C(0.6f, 0.6f, 1f), C(1f, 1f, 0.6f))

    val topMargin = 20f
    var selected = 0
    var fuelTick = 0f
    var pumping = false

    var volume = 0f

    var soundId = 0L

    override fun create(container: EntityContainer2D) {
        soundId = Assets.fuelUpLoop.loop(0f)

        super.create(container)
    }

    override fun render(stage: RenderStage) {
        if(selected >= ship.tanks) selected = 0

        notification = false

        DrawColor.draw(stage) {
            if(!armLocked) {
                Assets.prototype14.color = Color.WHITE
                string("The docking arm must be locked in place to refuel.", origin + screenSize /2f, Assets.prototype14, V2(0f))
            } else {
                val dX = (screenSize.x - 30f) / ship.tanks.toFloat()

                for (i in 0 until ship.tanks) {
                    val level = (ship.tankLevel[i] / ship.maxTankLevel[i])
                    val tankHeight = (screenSize.y - 40f - topMargin) * (ship.maxTankLevel[i] / 20f)

                    Draw.fill.color = tankColors[i] * 0.3f
                    sprite(Draw.fill, origin + V2(15f, 30f) + V2(dX * i, 0), V2(5f, tankHeight))
                    sprite(Draw.fill, origin + V2(15f, 30f) + V2(dX * (i + 1) - 5f, 0), V2(5f, tankHeight))
                    sprite(Draw.fill, origin + V2(15f, 30f) + V2(dX * i, 0), V2(dX, 5f))
                    sprite(Draw.fill, origin + V2(15f, 30f) + V2(dX * i, tankHeight), V2(dX, 5f))
                    Draw.fill.color = tankColors[i] * 0.05f
                    sprite(Draw.fill, origin + V2(20f, 35f) + V2(dX * i, 0f), V2(dX-10f, tankHeight - 5f))
                    Draw.fill.color = tankColors[i] * 0.5f
                    sprite(Draw.fill, origin + V2(20f, 35f) + V2(dX * i, 0f), V2(dX-10f, (tankHeight - 5f) * level))

                    Assets.prototype14.color = Color.WHITE
                    string("Tank ${tankNames[i]}", origin + V2(15f + dX * i + dX / 2f, 8f), Assets.prototype14, V2(0f, 1f))

                    if(selected == i) {
                        sprite(Assets.fuelArrow, origin + V2(15f + dX * i + dX / 2f, screenSize.y - 20f - fuelTick *20f) - Assets.fuelArrow.size/2f, Assets.fuelArrow.size)
                    }
                }
            }
        }
    }

    override fun update(delta: Float) {
        if(selected >= ship.tanks) selected = 0

        if(active() && pumping) {
            volume += (1f-volume)/4f
        } else {
            volume += (0f-volume)/10f
        }
        Assets.fuelUpLoop.setPitch(soundId, 0.5f + (ship.tankLevel[selected] / ship.maxTankLevel[selected])*0.8f)
        Assets.fuelUpLoop.setVolume(soundId, volume)

        if(armLocked && pumping && ship.tankLevel[selected] < ship.maxTankLevel[selected]) {
            fuelTick = (fuelTick + delta) % 1f
            ship.tankLevel[selected] += delta / 1.2f
            if(ship.tankLevel[selected] >= ship.maxTankLevel[selected]) {
                ship.tankLevel[selected] = ship.maxTankLevel[selected]
                notification = true
            }
        } else {
            fuelTick = 0f
        }

        super.update(delta)
    }

    val Previous = press("Previous") {
        selected -= 1
        if(selected < 0) selected = ship.tanks-1
    }

    val Next = press("Next") {
        selected += 1
        if(selected >= ship.tanks) selected = 0
    }

    val Pump = object : ButtonDefinition("Pump", true) {
        override fun pressed() {
            pumping = true
        }

        override fun depressed() {
            pumping = false
        }
    }

    override val buttons = listOf(
        disabled(), disabled(), disabled(),
        Previous, Pump, Next,
        disabled(), disabled(), disabled()
    )
}