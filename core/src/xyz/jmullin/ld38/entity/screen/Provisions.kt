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
import xyz.jmullin.ld38.entity.ButtonDefinition
import xyz.jmullin.ld38.entity.DockingArm.armLocked
import xyz.jmullin.ld38.entity.disabled
import xyz.jmullin.ld38.entity.press
import xyz.jmullin.ld38.entity.ship

object Provisions : Screen() {
    val supplyNames = listOf("Food", "Water", "Clothing", "Materials")
    val supplyColors = listOf(C(0.6f, 0.6f, 1f), C(0.6f, 0.6f, 1f), C(0.6f, 1f, 1f), C(0.8f, 0.6f, 0.3f))

    val topMargin = 20f
    var selected = 0
    var loadTick = 0f
    var loading = false

    var soundId = 0L
    var volume = 0f

    override fun create(container: EntityContainer2D) {
        soundId = Assets.fuelUpLoop.loop(0f)

        super.create(container)
    }

    override fun render(stage: RenderStage) {
        if(selected >= supplyNames.size) selected = 0

        notification = false

        DrawColor.draw(stage) {
            if(!armLocked) {
                Assets.prototype14.color = Color.WHITE
                string("The docking arm must be locked in place to resupply.", origin + screenSize /2f, Assets.prototype14, V2(0f))
            } else {
                val dX = (screenSize.x - 30f) / supplyNames.size.toFloat()

                for (i in 0 until supplyNames.size) {
                    val level = (ship.supply[i] / ship.maxSupply[i])
                    val supplyHeight = (screenSize.y - 40f - topMargin) * (ship.maxSupply[i] / 20f)

                    Draw.fill.color = supplyColors[i] * 0.3f
                    sprite(Draw.fill, origin + V2(15f, 30f) + V2(dX * i, 0), V2(5f, supplyHeight))
                    sprite(Draw.fill, origin + V2(15f, 30f) + V2(dX * (i + 1) - 5f, 0), V2(5f, supplyHeight))
                    sprite(Draw.fill, origin + V2(15f, 30f) + V2(dX * i, 0), V2(dX, 5f))
                    sprite(Draw.fill, origin + V2(15f, 30f) + V2(dX * i, supplyHeight), V2(dX, 5f))
                    Draw.fill.color = supplyColors[i] * 0.05f
                    sprite(Draw.fill, origin + V2(20f, 35f) + V2(dX * i, 0f), V2(dX-10f, supplyHeight - 5f))
                    Draw.fill.color = supplyColors[i] * 0.5f
                    sprite(Draw.fill, origin + V2(20f, 35f) + V2(dX * i, 0f), V2(dX-10f, (supplyHeight - 5f) * level))

                    Assets.prototype14.color = Color.WHITE
                    string(supplyNames[i], origin + V2(15f + dX * i + dX / 2f, 8f), Assets.prototype14, V2(0f, 1f))

                    if(selected == i) {
                        sprite(Assets.fuelArrow, origin + V2(15f + dX * i + dX / 2f, screenSize.y - 20f - loadTick *20f) - Assets.fuelArrow.size/2f, Assets.fuelArrow.size)
                    }
                }
            }
        }
    }

    override fun update(delta: Float) {
        if(selected >= supplyNames.size) selected = 0

        if(active() && loading) {
            volume += (1f-volume)/4f
        } else {
            volume += (0f-volume)/10f
        }
        Assets.fuelUpLoop.setPitch(soundId, 0.5f + (ship.supply[selected] / ship.maxSupply[selected])*0.8f)
        Assets.fuelUpLoop.setVolume(soundId, volume)

        if(armLocked && loading && ship.supply[selected] < ship.maxSupply[selected]) {
            loadTick = (loadTick + delta) % 1f
            ship.supply[selected] += delta / 1.2f
            if(ship.supply[selected] >= ship.maxSupply[selected]) {
                ship.supply[selected] = ship.maxSupply[selected]
                notification = true
            }
        } else {
            loadTick = 0f
        }

        super.update(delta)
    }

    val Previous = press("Previous") {
        selected -= 1
        if(selected < 0) selected = supplyNames.size-1
    }

    val Next = press("Next") {
        selected += 1
        if(selected >= supplyNames.size) selected = 0
    }

    val Load = object : ButtonDefinition("Load", true) {
        override fun pressed() {
            loading = true
        }

        override fun depressed() {
            loading = false
        }
    }

    override val buttons = listOf(
        disabled(), disabled(), disabled(),
        Previous, Load, Next,
        disabled(), disabled(), disabled()
    )
}