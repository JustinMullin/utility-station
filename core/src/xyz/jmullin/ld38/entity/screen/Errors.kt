package xyz.jmullin.ld38.entity.screen

import xyz.jmullin.drifter.entity.EntityContainer2D
import xyz.jmullin.drifter.extensions.*
import xyz.jmullin.drifter.rendering.Draw
import xyz.jmullin.drifter.rendering.RenderStage
import xyz.jmullin.drifter.rendering.sprite
import xyz.jmullin.drifter.sound.Play
import xyz.jmullin.ld38.Assets
import xyz.jmullin.ld38.Stage
import xyz.jmullin.ld38.Stage.DrawColor
import xyz.jmullin.ld38.entity.ButtonDefinition
import xyz.jmullin.ld38.entity.MessageLog
import xyz.jmullin.ld38.entity.disabled
import xyz.jmullin.ld38.entity.press

object Errors : Screen() {
    val errorLog = MessageLog(Errors, C(0.8f, 0.3f, 0.3f)).apply {
        position.set(V2(12f))
    }

    override fun create(container: EntityContainer2D) {
        add(errorLog)
        errorLog.position.set(origin)
        errorLog.size.set(screenSize - V2(12f))

        super.create(container)
    }

    override fun render(stage: RenderStage) {
        notification = false

        DrawColor.draw(stage) {
            errorLog.render(stage)
        }
    }

    fun log(message: String) {
        Play.sound(Assets.uhuh, 0.6f..0.8f, 0.9f..1f)
        errorLog.addMessage("- $message")
    }

    val Clear = press("Clear") {
        errorLog.clearMessages()
    }

    val Dismiss = press("Delete") {
        errorLog.killLastMessage()
    }

    override val buttons = listOf(
        disabled(), disabled(), disabled(),
        Clear, Dismiss, disabled(),
        disabled(), disabled(), disabled()
    )
}