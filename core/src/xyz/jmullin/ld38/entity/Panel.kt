package xyz.jmullin.ld38.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Vector2
import xyz.jmullin.drifter.entity.Entity2D
import xyz.jmullin.drifter.entity.EntityContainer2D
import xyz.jmullin.drifter.extensions.*
import xyz.jmullin.drifter.rendering.*
import xyz.jmullin.ld38.Assets
import xyz.jmullin.ld38.Stage.DrawColor
import xyz.jmullin.ld38.entity.ConsoleButton.Companion.vanishingPoint
import xyz.jmullin.ld38.entity.screen.*

/**
 * @author Justin Mullin
 */
class Panel : Entity2D() {
    val margin = 8f
    val panelHeight = 200f
    val perspectiveSkew = 20f
    val sidePanelWidth = 386f
    val buttonAreaHeight = 150f

    val buttonsInRow = 11
    val numButtonRows = 3
    val totalButtons = buttonsInRow * numButtonRows
    val rowAWidth = gameW() - 10f
    val buttonMargin = 20f
    val spacing = 15f
    val buttonWidth = (rowAWidth - spacing * (buttonsInRow -1) - buttonMargin*2f)/ buttonsInRow.toFloat()
    val rowSpacing = 15f
    val radioButtonSpacing = 22f
    val buttonHeight = 25f
    val buttonSize = V2(buttonWidth, buttonHeight)

    val radioButtonSize = V2(buttonHeight, buttonHeight)

    val panelBatch = SpriteBatch(1000)

    val screenBufferSize = V2(sidePanelWidth*2f- margin *4f, panelHeight- margin *2f)
    val buttonsPerRow = (screenBufferSize.x/buttonWidth).toInt()
    val buttonRows = totalButtons / buttonsPerRow
    val panelBufferSize = screenBufferSize + V2(0f, (buttonRows+2)*buttonHeight)

    val screenBufferYPct = screenBufferSize.y / panelBufferSize.y

    val panelBuffer = FrameBuffer(Pixmap.Format.RGBA8888, panelBufferSize.xI, panelBufferSize.yI, true)

    fun buttonRegion(i: Int): TextureRegion {
        val origin = V2(0f, screenBufferSize.y) + V2(i % buttonsPerRow, i / buttonsPerRow) * buttonSize
        return TextureRegion(panelBuffer.colorBufferTexture, origin.xI, origin.yI, buttonWidth.toInt(), buttonHeight.toInt())
    }

    fun activeLeft() = screens.getOrNull(leftScreenSelection.value)
    fun activeRight() = screens.getOrNull(rightScreenSelection.value + 4)

    val panelScreenSize = screenBufferSize * V2(0.5f, 1f)

    val camera = OrthographicCamera(panelBuffer.width.toFloat(), panelBuffer.height.toFloat())

    var buttons = listOf<Button>()

    override fun create(container: EntityContainer2D) {
        camera.position.set(camera.viewportWidth/2f, camera.viewportHeight/2f, 0f)
        camera.update()

        var i = 0
        var j = 0

        for(bX in -buttonsInRow /2..buttonsInRow /2) {
            if(j != 3 && j != 7) {
                addConsoleButton(
                    mainButtons[i],
                    V2(gameW()/2f-buttonWidth/2f+(spacing+buttonWidth)*bX, rowSpacing),
                    V2(gameW()/2f+buttonWidth/2f+(spacing+buttonWidth)*bX, rowSpacing),
                    buttonRegion(i),
                    buttonHeight)
                i += 1
            }
            j += 1
        }

        buttons.take(buttonsInRow-2).map { button ->
            button as ConsoleButton
            addConsoleButton(
                mainButtons[i],
                button.b + V2(((vanishingPoint-button.b).nor() * rowSpacing).x, rowSpacing),
                button.c + V2(((vanishingPoint-button.c).nor() * rowSpacing).x, rowSpacing),
                buttonRegion(i),
                buttonHeight)
            i += 1
        }

        buttons.drop(buttonsInRow-2).take(buttonsInRow).map { button ->
            button as ConsoleButton
            addConsoleButton(
                mainButtons[i],
                button.b + V2(((vanishingPoint-button.b).nor() * rowSpacing).x, rowSpacing),
                button.c + V2(((vanishingPoint-button.c).nor() * rowSpacing).x, rowSpacing),
                buttonRegion(i),
                buttonHeight)
            i += 1
        }

        for(id in 0 until 4) {
            addWallButton(id, id, leftRadioButtons[id], leftScreenSelection,
                V2(sidePanelWidth + margin, buttonAreaHeight + perspectiveSkew + 11f + (radioButtonSize.y+radioButtonSpacing) * id), radioButtonSize)

            addWallButton(id, id+4, rightRadioButtons[id], rightScreenSelection,
                V2(gameW() - sidePanelWidth - margin - radioButtonSize.x, buttonAreaHeight + perspectiveSkew + 11f + (radioButtonSize.y+radioButtonSpacing) * id), radioButtonSize)
        }

        screens.forEachIndexed { id, screen ->
            if(id < 4) {
                screen.origin.set(V2(0f))
            } else {
                screen.origin.set(V2(panelScreenSize.x, 0f))
            }

            screen.screenSize.set(panelScreenSize)

            add(screen)
        }

        super.create(container)
    }

    override fun render(stage: RenderStage) {
        camera.update()

        DrawColor.draw(stage) {
            stage.batch.end()
            DrawColor.buffer.end()
            panelBuffer.begin()
            panelBatch.projectionMatrix = camera.combined
            panelBatch.begin()
            Gdx.gl.glClearColor(1f, 1f, 1f, 0f)
            Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
            Gdx.gl20.glEnable(GL20.GL_BLEND)
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

            val oldBatch = stage.batch
            stage.batch = panelBatch

            screens.forEachIndexed { id, screen ->
                if(leftScreenSelection.value == id || (rightScreenSelection.value != -1 && rightScreenSelection.value + 4 == id)) {
                    screen.render(stage)
                }
            }

            buttons.forEachIndexed { i, button ->
                when(button) {
                    is ConsoleButton -> button.drawLabel(stage)
                }
            }
            stage.batch = oldBatch

            panelBatch.end()
            stage.batch.begin()
            panelBuffer.end()
            DrawColor.buffer.begin()

            sprite(Assets.console, V2(0f), V2(1024f, 170f))
            sprite(Assets.topper, V2(0f, buttonAreaHeight+panelHeight), V2(1024f, 45f))

            quad(Assets.leftPanel,
                V2(0f, buttonAreaHeight+panelHeight),
                V2(0f, buttonAreaHeight),
                V2(sidePanelWidth, buttonAreaHeight+perspectiveSkew),
                V2(sidePanelWidth, buttonAreaHeight+panelHeight+perspectiveSkew),
                Color.WHITE)
            quad(Assets.midPanel,
                V2(sidePanelWidth, buttonAreaHeight+panelHeight+perspectiveSkew),
                V2(sidePanelWidth, buttonAreaHeight+perspectiveSkew),
                V2(gameW()-sidePanelWidth, buttonAreaHeight+perspectiveSkew),
                V2(gameW()-sidePanelWidth, buttonAreaHeight+panelHeight+perspectiveSkew),
                Color.WHITE)
            quad(Assets.rightPanel,
                V2(gameW()-sidePanelWidth, buttonAreaHeight+panelHeight+perspectiveSkew),
                V2(gameW()-sidePanelWidth, buttonAreaHeight+perspectiveSkew),
                V2(gameW(), buttonAreaHeight),
                V2(gameW(), buttonAreaHeight+panelHeight),
                Color.WHITE)

            quad(panelBuffer.colorBufferTexture,
                V2(margin, buttonAreaHeight+ margin), V2(margin, buttonAreaHeight+panelHeight- margin), V2(sidePanelWidth- margin, buttonAreaHeight+panelHeight+perspectiveSkew- margin), V2(sidePanelWidth- margin, buttonAreaHeight+perspectiveSkew+ margin),
                V2(0f, 0f), V2(0f, screenBufferYPct), V2(0.5f, screenBufferYPct), V2(0.5f, 0f))

            quad(panelBuffer.colorBufferTexture,
                V2(gameW()-sidePanelWidth+ margin, buttonAreaHeight+perspectiveSkew+ margin), V2(gameW()-sidePanelWidth+ margin, buttonAreaHeight+panelHeight+perspectiveSkew- margin), V2(gameW()- margin, buttonAreaHeight+panelHeight- margin), V2(gameW()- margin, buttonAreaHeight+ margin),
                V2(0.5f, 0f), V2(0.5f, screenBufferYPct), V2(1f, screenBufferYPct), V2(1f, 0f))

            buttons.forEachIndexed { i, button ->
                button.render(stage)
            }
        }
    }

    fun addWallButton(id: Int, globalId: Int, definition: ButtonDefinition, radioValue: RadioValue, v: Vector2, bSize: Vector2) {
        val button = WallButton(id, globalId, definition, radioValue, v, bSize)
        add(button)
        buttons += button
    }

    fun addConsoleButton(definition: ButtonDefinition, left: Vector2, right: Vector2, region: TextureRegion, height: Float) {
        val button = ConsoleButton(definition, left, right, panelBufferSize, region, height)
        add(button)
        buttons += button
    }
}