package xyz.jmullin.ld38.entity

import xyz.jmullin.drifter.animation.Trigger
import xyz.jmullin.drifter.animation.delay
import xyz.jmullin.drifter.entity.Entity2D
import xyz.jmullin.drifter.entity.EntityContainer2D
import xyz.jmullin.drifter.extensions.rFloat
import xyz.jmullin.ld38.data.ShipDefinition
import xyz.jmullin.ld38.entity.screen.Comms.status
import xyz.jmullin.ld38.entity.screen.Performance
import xyz.jmullin.ld38.entity.screen.Performance.started

class MainController : Entity2D() {

    override fun create(container: EntityContainer2D) {
        delay(0.5f) {
            Performance.log("Good morning! Oh...please, don't fret. The reprogramming will take a minute to kick in.") then delay(1.5f) {
                Performance.log("...did you forget? You've been promoted! You're the new Executive Station Operator at Utility Station OR-152. Congratulations!") then delay(2f) {
                    Performance.log("This entire 3x3 meter facility is yours! We don't give that kind of space to everyone. Be proud.") then delay(2f) {
                        Performance.log("Anyway, none of that is important right now. There's work to be done. Speaking of which...") then delay(2f) {
                            Performance.log("${ship.callsign.capitalize()} has arrived at the station. Remember your training...I'm sure it'll come back to you. We have high hopes for you. Please don't screw this up.")
                            delay(1f) {
                                started = true
                                newShip()
                            } go(this)
                        }
                    }
                }
            }
        } go(this)



        super.create(container)
    }

    override fun update(delta: Float) {
        if(started && parent?.children?.find { it is Ship } == null) {
            newShip()

            if(!ship.approvedToLeave)
                when(ship.timeWarnings) {
                    0 -> if(ship.waitTime() > 180) {
                        ship.timeWarnings += 1
                        Performance.log("${ship.callsign.capitalize()} has been waiting a long time. Please pick up the pace.")
                    }
                    1 -> if(ship.waitTime() > 360) {
                        ship.timeWarnings += 1
                        Performance.demerits += 1
                        Performance.log("We are starting to receive complaints from ${ship.callsign} about the delays. Are you about done?")
                    }
                    2 -> if(ship.waitTime() > 640) {
                        ship.timeWarnings += 1
                        Performance.demerits += 1
                        Performance.log("This delay is becoming extraordinary. Please finish up and allow ${ship.callsign} to get on their way.")
                    }
                }
        }

        super.update(delta)
    }

    fun newShip() {
        val entity = Ship()
        shipEntity = entity
        ship = entity.definition
        parent?.add(entity)
    }
}

var shipEntity: Ship = Ship()
var ship: ShipDefinition = ShipDefinition()