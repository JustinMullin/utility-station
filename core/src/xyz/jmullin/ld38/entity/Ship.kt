package xyz.jmullin.ld38.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import xyz.jmullin.drifter.animation.Easing
import xyz.jmullin.drifter.animation.delay
import xyz.jmullin.drifter.animation.tween
import xyz.jmullin.drifter.entity.Entity2D
import xyz.jmullin.drifter.entity.EntityContainer2D
import xyz.jmullin.drifter.extensions.*
import xyz.jmullin.drifter.rendering.RenderStage
import xyz.jmullin.drifter.rendering.sprite
import xyz.jmullin.ld38.Assets
import xyz.jmullin.ld38.Stage.DrawColor
import xyz.jmullin.ld38.data.DecontaminationRequirement
import xyz.jmullin.ld38.data.Dialogue
import xyz.jmullin.ld38.data.RepairRequirement
import xyz.jmullin.ld38.data.ShipDefinition
import xyz.jmullin.ld38.entity.DockingArm.armLocked
import xyz.jmullin.ld38.entity.screen.Comms
import xyz.jmullin.ld38.entity.screen.Comms.status
import xyz.jmullin.ld38.entity.screen.Scan

class Ship : Entity2D() {
    val definition = ShipDefinition()

    init {
        depth = 10
    }

    var nearStation = false
    var approachingRange = false
    var dockingRange = false
    var leaving = false

    val shipSprite = listOf(Assets.ship1, Assets.ship2, Assets.ship3)[definition.makeIndex]
    val scanSprite = listOf(Assets.ship1Scan, Assets.ship2Scan, Assets.ship3Scan)[definition.makeIndex]
    val pixmap = listOf(Assets.ship1Pixmap, Assets.ship2Pixmap, Assets.ship3Pixmap)[definition.makeIndex]
    val spriteSize = shipSprite.size

    val origin = V2(-spriteSize.x/2f, gameH()-50f)

    override fun create(container: EntityContainer2D) {
        Scan.reset()

        position.set(origin)
        tween(rFloat(8f, 10f), Easing.Companion.Sine) { n ->
            position.set(origin.x + n * (gameW() + spriteSize.x)/4f, position.y)
        } then {
            nearStation = true
        } go(this)

        populate(rInt(10, 30)) { v ->
            definition.addCrew(v)
        }
        if(definition.requirements.contains(DecontaminationRequirement)) {
            populate(rInt(5, 10)) { v ->
                definition.addContamination(v)
            }
        }
        if(definition.requirements.contains(RepairRequirement)) {
            populate(rInt(5, 10)) { v ->
                definition.addDamage(v)
            }
        }

        super.create(container)
    }

    fun populate(n: Int, action: (Vector2) -> Unit) {
        for(i in 0 until n) {
            var found = false
            var queryCount = 0

            while(!found && queryCount < 10) {
                queryCount += 1
                val query = rV(V2(10f), pixmap.size - V2(20f))
                val color = Color(pixmap.getPixel(query.x.toInt(), pixmap.height - query.y.toInt()))

                if(color.a > 0.1f) {
                    action(query)
                    found = true
                }
            }
        }
    }

    override fun update(delta: Float) {
        if(definition.approvedToLeave) {
            if(!leaving && !armLocked) {
                Comms.incoming(Dialogue.departureInitiated()) then delay(0.5f) {
                    if(ship.commsOpen) {
                        status("CHANNEL TERMINATED") then {
                            ship.commsOpen = false
                        }
                    }
                } then tween(rFloat(8f, 10f), Easing.Companion.Sine) { n ->
                    position.set(origin.x + (gameW() + spriteSize.x) * (0.5f+n*0.5f), position.y)
                } then {
                    definition.demeritsForMissedRequirements()
                    remove()
                }
                leaving = true
            }
        } else if(!dockingRange && !approachingRange && definition.approvedToDock) {
            approachingRange = true
            tween(rFloat(8f, 10f), Easing.Companion.Sine) { n ->
                position.set(origin.x + (gameW() + spriteSize.x)/4f + n * (gameW() + spriteSize.x)/4f, position.y)
            } go(this)
        }

        super.update(delta)
    }

    override fun render(stage: RenderStage) {
        DrawColor.draw(stage) {
            sprite(shipSprite, position - spriteSize/2f, spriteSize)
        }

        super.render(stage)
    }
}