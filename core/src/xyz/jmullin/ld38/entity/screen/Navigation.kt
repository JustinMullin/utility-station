package xyz.jmullin.ld38.entity.screen

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.math.Vector2
import xyz.jmullin.drifter.extensions.*
import xyz.jmullin.drifter.extensions.FloatMath.max
import xyz.jmullin.drifter.extensions.FloatMath.min
import xyz.jmullin.drifter.rendering.Draw
import xyz.jmullin.drifter.rendering.RenderStage
import xyz.jmullin.drifter.rendering.sprite
import xyz.jmullin.drifter.rendering.string
import xyz.jmullin.drifter.sound.Play
import xyz.jmullin.ld38.Assets
import xyz.jmullin.ld38.Stage.DrawColor
import xyz.jmullin.ld38.data.Stars
import xyz.jmullin.ld38.entity.disabled
import xyz.jmullin.ld38.entity.press
import xyz.jmullin.ld38.entity.ship

object Navigation : Screen() {
    data class Star(val v: Vector2, val name: String, val color: Color, val difficulty: Float)

    var names = listOf("")

    val density = 8f

    var calculating = false
    var calculations = 0f
    var calculated = ""
    var labelA = 0f
    var fadeIn = 0f

    val stars = (0 until density.toInt()).map { x ->
        (0 until density.toInt()).mapNotNull { y ->
            val pos = (V2(x, y) - V2(density/2f))
            if(pos.len() <= density/2f && probability(0.5f)) {
                var name = ""
                while(names.contains(name)) {
                    name = Stars.randomName()
                }
                names += name
                Star(V2(x, y) + rV(V2(-0.4f), V2(0.4f)), name, rColor(0.8f, 1f), rFloat(pos.len()*2f, pos.len()*3f))
            } else null
        }
    }.flatten().sortedBy { it.v.x + rFloat(-0.1f, 0.1f) }

    val dVScaled: Vector2 get() = (screenSize - V2(8f)) / density
    val dV: Vector2 get() = V2(dVScaled.minComponent)
    val offset: Vector2 get() = screenSize - V2(density)*dV

    var selected = rInt(stars.size)
    var highlightedName = ""
    val highlightV = screenSize/2f

    override fun render(stage: RenderStage) {
        notification = false

        DrawColor.draw(stage) {
            stars.forEachIndexed { i, star ->
                Assets.star1.color = star.color
                sprite(Assets.star1, origin + offset/2f + dV*star.v - Assets.star1.size/2f, Assets.star1.size)
            }

            sprite(Assets.starSelector, highlightV - Assets.starSelector.size/2f, Assets.starSelector.size)

            val align = V2(
                if(highlightV.x > origin.x + screenSize.x/2f) -1f else 1f,
                if(highlightV.y > origin.y + screenSize.y/2f) -1f else 1f
            )
            Assets.prototype14.color = C(Color.WHITE, labelA)
            val layout = GlyphLayout(Assets.prototype14, highlightedName)
            Draw.fill.color = C(C(0.2f), labelA)
            sprite(Draw.fill, highlightV + align*7f, align * V2(layout.width, layout.height) + align*10f)
            string(highlightedName, highlightV + align*12f, Assets.prototype14, align)

            Draw.fill.color = C(0.3f, 0.3f, 0.4f, fadeIn)
            sprite(Draw.fill, origin + screenSize/2f - V2(180f, 26f), V2(360f, 52f))
            Draw.fill.color = C(0.2f, 0.2f, 0.3f, fadeIn)
            sprite(Draw.fill, origin + screenSize/2f - V2(175, 21f), V2(350, 42f))
            val dots = (0..(calculations % 3f).toInt()).map { "." }.joinToString("")
            Assets.prototype18.color = C(Color.WHITE, fadeIn)

            if(calculated != "") {
                if(ship.course != "") {
                    string("Course transmitted to ${ship.callsign}.", origin + V2(26f, screenSize.y/2f), Assets.prototype18, V2(1f, 0f))
                } else {
                    string("Calculations to $calculated ready.", origin + V2(26f, screenSize.y/2f), Assets.prototype18, V2(1f, 0f))
                }
            } else {
                string("Calculating course to $highlightedName$dots", origin + V2(26f, screenSize.y/2f), Assets.prototype18, V2(1f, 0f))
            }
        }
    }

    override fun update(delta: Float) {
        if(calculating) {
            fadeIn += (1f-fadeIn)/10f
        } else {
            fadeIn *= 0.9f
        }

        stars.getOrNull(selected)?.let { highlighted ->
            highlightedName = highlighted.name

            if(calculating && calculated == "") {
                calculations += delta
                if(calculations >= highlighted.difficulty) {
                    Play.sound(Assets.calculated, 0.6f..0.8f, 0.9f..1f)
                    calculated = highlighted.name
                    notification = true
                }
            }

            val targetV = origin + offset/2f + dV*highlighted.v
            highlightV.add((targetV - highlightV)/4f)

            if((highlightV - targetV).len() < 5f) {
                labelA += (1f - labelA) / 10f
            } else {
                labelA = 0f
            }
        }

        super.update(delta)
    }

    val Previous = press("Previous") {
        if(!calculating) {
            reset()
            selected -= 1
            labelA = 0f
            if(selected < 0) selected = stars.size-1
        }
    }

    val Next = press("Next") {
        if(!calculating) {
            reset()
            selected += 1
            labelA = 0f
            if(selected >= stars.size) selected = 0
        }
    }

    val Calculate = press("Calculate") {
        if(!calculating) {
            Play.sound(Assets.request, 0.6f..0.8f, 0.9f..1f)
        }
        calculating = true
    }

    val Cancel = press("Cancel") {
        reset()
        ship.course = ""
    }

    val Transmit = press("Transmit") {
        if(calculated != "") {
            if(ship.course == "") {
                Play.sound(Assets.transmit, 0.6f..0.8f, 0.9f..1f)
            }
            ship.course = calculated
        } else {
            Errors.log("No course is prepared to transmit to ${ship.callsign}.")
        }
    }

    fun reset() {
        calculated = ""
        calculating = false
        calculations = 0f
    }

    override val buttons = listOf(
        disabled(), Transmit, disabled(),
        Previous, Calculate, Next,
        disabled(), Cancel, disabled()
    )
}