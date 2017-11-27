package xyz.jmullin.ld38.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import org.funktionale.collections.prependTo
import xyz.jmullin.drifter.animation.*
import xyz.jmullin.drifter.entity.Entity2D
import xyz.jmullin.drifter.extensions.*
import xyz.jmullin.drifter.rendering.RenderStage
import xyz.jmullin.ld38.Assets
import xyz.jmullin.ld38.entity.screen.Screen
import xyz.jmullin.skein.message.MessageBox

class MessageLog(val screen: Screen, val defaultColor: Color = Color.WHITE) : Entity2D() {
    var messages = listOf<MessageBox>()

    fun addMessage(message: String, color: Color? = null): Event {
        screen.notification()
        val messageBox = MessageBox(Assets.prototype14, color ?: defaultColor, 1.02f)
        messageBox.position.set(position + V2(0f, size.y))
        messageBox.size.x = size.x
        messages = messageBox.prependTo(messages)
        return messageBox.showMessage(message)
    }

    fun animateRemoval(messageBox: MessageBox): Trigger {
        return { messageBox.dying = true } then tween(0.5f) { n ->
            messageBox.color = C(Color.WHITE, 1f-n)
        } then {
            messageBox.remove()
            messages -= messageBox
        }
    }

    fun clearMessages() {
        messages.forEach { messageBox ->
            animateRemoval(messageBox) go(messageBox)
        }
    }

    fun killLastMessage() {
        messages.find { !it.dying }?.let { messageBox ->
            animateRemoval(messageBox) go(messageBox)
        }
    }

    override fun render(stage: RenderStage) {
        var y = size.y

        for(i in 0 until messages.size) {
            val messageBox = messages[i]
            val boxHeight = messageBox.messageLines.size * messageBox.font.lineHeight * messageBox.lineHeightMultiplier
            messageBox.position.add((V2(position.x + 12f, y - boxHeight) - messageBox.position) / 10f)
            messageBox.size.set(V2(size.x, boxHeight))
            y -= boxHeight + 5f
            if(messageBox.y + boxHeight <= size.y+2f && messageBox.y + boxHeight >= 0f) {
                messageBox.render(stage)
            }
        }
    }

    override fun update(delta: Float) {
        messages.forEach { messageBox ->
            val boxHeight = messageBox.messageLines.size * messageBox.font.lineHeight * messageBox.lineHeightMultiplier
            if(messageBox.y + boxHeight <= size.y+2f) {
                messageBox.update(delta)
            }
        }

        super.update(delta)
    }
}