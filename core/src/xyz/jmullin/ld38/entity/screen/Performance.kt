package xyz.jmullin.ld38.entity.screen

import com.badlogic.gdx.graphics.Color
import xyz.jmullin.drifter.animation.Event
import xyz.jmullin.drifter.entity.EntityContainer2D
import xyz.jmullin.drifter.extensions.*
import xyz.jmullin.drifter.rendering.RenderStage
import xyz.jmullin.drifter.rendering.string
import xyz.jmullin.drifter.sound.Play
import xyz.jmullin.ld38.Assets
import xyz.jmullin.ld38.Stage.DrawColor
import xyz.jmullin.ld38.entity.disabled
import xyz.jmullin.ld38.entity.ship
import xyz.jmullin.skein.message.MessageBox

object Performance : Screen() {
    var demerits = 0
    val messageBox = MessageBox(Assets.prototype14, lineHeightMultiplier = 1f, color = C(1f, 1f, 0.5f))

    var started = false
    var fade = 0f

    fun grade(): Pair<String, Color> {
        return when(demerits) {
            0 -> "A+" to Color.GREEN
            1 -> "A" to Color.GREEN
            2 -> "A-" to Color.CYAN
            3 -> "B" to Color.BLUE
            5 -> "B-" to Color.BLUE
            8 -> "C" to Color.YELLOW
            13 -> "C-" to Color.YELLOW
            21 -> "D" to Color.ORANGE
            34 -> "D-" to Color.ORANGE
            else -> "F" to Color.RED
        }
    }

    override fun create(container: EntityContainer2D) {
        messageBox.apply {
            position.set(V2(15f))
            size.set(V2(screenSize.x - 30f, 60f))
        }
        add(messageBox)

        super.create(container)
    }

    fun log(message: String): Event {
        if(active()) Play.sound(Assets.hmm, 0.3f..0.5f, 0.9f..1f)
        notification = true
        messageBox.clearMessage()
        return messageBox.showMessage("MESSAGE FROM CENTRAL: $message")
    }

    override fun render(stage: RenderStage) {
        val font = Assets.prototype14
        font.color = Color.WHITE.alpha(fade)

        notification = false

        DrawColor.draw(stage) {
            var y = screenSize.y-15f
            string("Now serving: ${ship.make} ${ship.callsign}", V2(15f, y), font, V2(1f, -1f))
            y -= font.lineHeight*1.1f
            string("Time at station: ${ship.formatWaitTime()}", V2(15f, y), font, V2(1f, -1f))
            y -= font.lineHeight*1.1f
            string("Total demerits received: $demerits", V2(15f, y), font, V2(1f, -1f))
            y -= font.lineHeight

            val (grade, color) = grade()
            Assets.steelfish48.color = color.alpha(fade)
            string(grade, position + screenSize - V2(15f), Assets.steelfish48, V2(-1f, -1f))
            string("GRADE", position + screenSize - V2(32f, 15f+Assets.steelfish48.lineHeight), Assets.prototype14, V2(0f, -1f))

            messageBox.size.y = messageBox.messageLines.size * messageBox.font.lineHeight * messageBox.lineHeightMultiplier
            messageBox.render(stage)
        }
    }

    override fun update(delta: Float) {
        if(started) fade += (1f - fade)/10f

        super.update(delta)
    }

    override val buttons = listOf(
        disabled(), disabled(), disabled(),
        disabled(), disabled(), disabled(),
        disabled(), disabled(), disabled()
    )
}