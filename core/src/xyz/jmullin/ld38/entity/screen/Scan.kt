package xyz.jmullin.ld38.entity.screen

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack
import xyz.jmullin.drifter.animation.Easing
import xyz.jmullin.drifter.animation.delay
import xyz.jmullin.drifter.animation.tween
import xyz.jmullin.drifter.entity.EntityContainer2D
import xyz.jmullin.drifter.extensions.*
import xyz.jmullin.drifter.extensions.FloatMath.pow
import xyz.jmullin.drifter.rendering.RenderStage
import xyz.jmullin.drifter.rendering.sprite
import xyz.jmullin.drifter.sound.Play
import xyz.jmullin.ld38.Assets
import xyz.jmullin.ld38.Stage.DrawColor
import xyz.jmullin.ld38.entity.disabled
import xyz.jmullin.ld38.entity.press
import xyz.jmullin.ld38.entity.ship
import xyz.jmullin.ld38.entity.shipEntity
import xyz.jmullin.skein.message.MessageBox

object Scan : Screen() {

    var structuralScan = 0f
    var bioScan = 0f
    var bioScanFade = 1f
    var lifeScan = 0f
    var lifeScanFade = 1f
    var damageScan = 0f
    var damageScanFade = 1f
    var cargoScan = 0f
    var structuralOnly = false
    val offset = V2(0f)

    fun reset() {
        structuralScan = 0f
        bioScan = 0f
        bioScanFade = 1f
        lifeScan = 0f
        lifeScanFade = 1f
        cargoScan = 0f
        damageScan = 0f
        damageScanFade = 1f
    }

    val messageBox = MessageBox(Assets.prototype14, lineHeightMultiplier = 1f, color = C(1f))

    val scissors = Rectangle()
    val clipBounds = Rectangle()

    override fun create(container: EntityContainer2D) {
        messageBox.apply {
            position.set(V2(15f))
            size.set(screenSize - 30f)
        }
        add(messageBox)

        super.create(container)
    }

    override fun render(stage: RenderStage) {
        val scanSprite = shipEntity.scanSprite

        DrawColor.draw(stage) {

            drawScan(structuralScan, this) {
                sprite(scanSprite, origin + offset + screenSize/2f - scanSprite.size/2f, scanSprite.size)
            }
            flush()

            drawScan(lifeScan, this) {
                Assets.personIcon.color = C(0.5f, 0.5f, 1f, lifeScanFade)
                ship.crew.forEach { v ->
                    sprite(Assets.personIcon, origin + offset + screenSize/2f - scanSprite.size/2f + v - Assets.personIcon.size/2f, Assets.personIcon.size)
                }
            }

            drawScan(bioScan, this) {
                Assets.bioIcon.color = Color.GREEN.alpha(bioScanFade)
                ship.contaminations.forEach { con ->
                    sprite(Assets.bioIcon, origin + offset + screenSize/2f - scanSprite.size/2f + con.v - Assets.bioIcon.size/2f, Assets.bioIcon.size)
                }
            }

            drawScan(damageScan, this) {
                Assets.damage.color = Color.WHITE.alpha(damageScanFade)
                ship.damages.forEach { con ->
                    sprite(Assets.damage, origin + offset + screenSize/2f - scanSprite.size/2f + con.v - Assets.damage.size/2f, Assets.damage.size)
                }
            }

            drawScan(cargoScan, this) {}
            flush()

            flush()

            if(!structuralOnly) {
                messageBox.render(stage)
            }
        }
    }

    fun drawScan(alpha: Float, batch: SpriteBatch, draw: () -> Unit) {
        if(alpha*screenSize.x > 1f) {
            batch.flush()

            clipBounds.set(origin.x, origin.y, screenSize.x * alpha, screenSize.y)

            ScissorStack.calculateScissors(layer()?.camera, batch.transformMatrix, clipBounds, scissors)
            ScissorStack.pushScissors(scissors)

            draw()

            batch.flush()
            ScissorStack.popScissors()

            if(alpha * screenSize.x < screenSize.x-1f && !structuralOnly) {
                val barBrightness = (pow(4f, 3f) * pow(alpha, 2f) * pow(1f - alpha, 2f))/4f
                Assets.scanBeam.color = C(Color.WHITE, barBrightness)
                batch.sprite(Assets.scanBeam, origin + offset + V2(screenSize.x * alpha, screenSize.y/2f - 64f), V2(10f, 128f))
            }
        }
    }

    val Calibrate = press("Calibrate") {
        message("Calibrating sensors to spacecraft...")
        Play.sound(Assets.scan, 0.6f..0.7f, 0.6f..1f)
        tween(3f, Easing.Companion.Sine) { n ->
            structuralScan = n
        } go(this)
    }

    val Bio = press("Bio") {
        bioScanFade = 1f
        message("Scanning for biological contamination...")

        Play.sound(Assets.scan, 0.6f..0.7f, 0.6f..1f)
        tween(3f, Easing.Companion.Sine) { n ->
            bioScan = n
        } then {
            if(ship.contaminations.isEmpty()) message("No biological contamination found.")
        } then tween(30f) { n ->
            bioScanFade = 1f-n
        } go(this)
    }

    val Structural = press("Structural") {
        damageScanFade = 1f
        message("Scanning for structural damage...")

        Play.sound(Assets.scan, 0.6f..0.7f, 0.6f..1f)
        tween(3f, Easing.Companion.Sine) { n ->
            damageScan = n
        } then {
            if(ship.damages.isEmpty()) message("No structural damage found.")
        } then tween(30f) { n ->
            damageScanFade = 1f-n
        } go(this)
    }

    val Life = press("Life") {
        lifeScanFade = 1f
        message("Scanning for life signs...")

        Play.sound(Assets.scan, 0.6f..0.7f, 0.6f..1f)
        tween(3f, Easing.Companion.Sine) { n ->
            lifeScan = n
        } then tween(30f) { n ->
            lifeScanFade = 1f-n
        } go(this)
    }

    val Cargo = press("Navigation") {
        message("Scanning cargo holds...")

        tween(8f, Easing.Companion.Sine) { n ->
            cargoScan = n
        } go(this)

        delay(7f) {
            message("Navigation holds contain no illegal goods.")
        } go(this)

    }

    fun message(str: String) {
        messageBox.clearMessage()
        messageBox.showMessage(str)
    }

    override val buttons = listOf(
        Bio, Calibrate, Life,
        disabled(), disabled(), Structural,
        disabled(), disabled(), disabled()
    )
}