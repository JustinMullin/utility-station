package xyz.jmullin.ld38.entity.screen

import xyz.jmullin.drifter.animation.Trigger
import xyz.jmullin.drifter.animation.delay
import xyz.jmullin.drifter.animation.event
import xyz.jmullin.drifter.entity.EntityContainer2D
import xyz.jmullin.drifter.extensions.C
import xyz.jmullin.drifter.extensions.V2
import xyz.jmullin.drifter.extensions.minus
import xyz.jmullin.drifter.rendering.RenderStage
import xyz.jmullin.drifter.sound.Play
import xyz.jmullin.ld38.Assets
import xyz.jmullin.ld38.Stage.DrawColor
import xyz.jmullin.ld38.data.Dialogue
import xyz.jmullin.ld38.entity.DockingArm.armLocked
import xyz.jmullin.ld38.entity.MessageLog
import xyz.jmullin.ld38.entity.disabled
import xyz.jmullin.ld38.entity.press
import xyz.jmullin.ld38.entity.ship

object Comms : Screen() {
    val messageLog = MessageLog(Comms, C(0.8f, 0.8f, 1f)).apply {
        position.set(V2(0f, 8f))
    }

    var talking = false

    override fun create(container: EntityContainer2D) {
        add(messageLog)
        messageLog.size.set(screenSize - V2(16f))

        super.create(container)
    }

    override fun render(stage: RenderStage) {
        notification = false

        DrawColor.draw(stage) {
            messageLog.render(stage)
        }
    }

    fun status(message: String): Trigger {
        if(active()) Play.sound(Assets.hello, 0.6f..0.8f, 0.9f..1f)
        return messageLog.addMessage("** $message **", C(1f, 1f, 0.7f))
    }

    fun incoming(message: String): Trigger {
        if(active()) Play.sound(Assets.hmm, 0.3f..0.5f, 0.9f..1f)
        return messageLog.addMessage("- $message")
    }

    fun outgoing(message: String): Trigger {
        if(active()) Play.sound(Assets.chirp, 0.6f..0.8f, 0.9f..1f)
        if(talking) {
            Errors.log("An outgoing message is currently being sent.")
            return event {}
        }
        talking = true
        return messageLog.addMessage("- $message", C(0.7f, 1f, 0.7f)) then { talking = false }
    }

    val Clear = press("Clear") {
        messageLog.clearMessages()
    }

    val Dismiss = press("Delete") {
        messageLog.killLastMessage()
    }

    val Requests = press("Requests") {
        if(ship.commsOpen) {
            if(ship.statusTimes > 0) {
                outgoing(Dialogue.remindMe()) then delay(2f) {
                    ship.requests()
                }
            } else {
                outgoing(Dialogue.requirements()) then delay(2f) {
                    ship.requests()
                }
            }
        } else {
            Errors.log("Communication channels are not currently open.")
        }
    }

    val Open = press("Open") {
        if(!ship.commsOpen) {
            status("CHANNEL OPENED") then {
                ship.commsOpen = true
                outgoing(Dialogue.hail()) then delay(1f) {
                    ship.hail()
                }
            }
        } else {
            Errors.log("Communication channels are already open.")
        }
    }

    val Close = press("Close") {
        if(ship.commsOpen) {
            status("CHANNEL TERMINATED") then {
                ship.commsOpen = false
            }
        } else {
            Errors.log("Communication channels are not currently open.")
        }
    }

    val Dock = press("Dock") {
        if(ship.commsOpen) {
            outgoing(Dialogue.dockApproved()) then delay(0.75f) {
                if(!ship.approvedToDock) {
                    ship.approvedToDock = true
                    incoming(Dialogue.dockInitiated())
                } else {
                    incoming(Dialogue.alreadyDocking())
                }
            }
        } else {
            Errors.log("Communication channels are not currently open.")
        }
    }

    val Depart = press("Depart") {
        if(ship.commsOpen) {
            outgoing(Dialogue.departure()) then delay(0.75f) {
                if (ship.docked) {
                    if(armLocked) incoming(Dialogue.departureArmLocked())
                    ship.approvedToLeave = true
                } else {
                    incoming(Dialogue.haventDockedYet())
                }
            }
        } else {
            Errors.log("Communication channels are not currently open.")
        }
    }

    val Destination = press("Destination") {
        if(ship.commsOpen) {
            outgoing(Dialogue.destination()) then delay(0.75f) {
                incoming(Dialogue.listDestination())
            }
        } else {
            Errors.log("Communication channels are not currently open.")
        }
    }

    override val buttons = listOf(
        Dock, Requests, Open,
        Depart, Destination, Close,
        disabled(), Clear, Dismiss
    )
}