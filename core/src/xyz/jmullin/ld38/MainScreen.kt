package xyz.jmullin.ld38

import com.badlogic.gdx.graphics.Color
import xyz.jmullin.drifter.application.DrifterScreen
import xyz.jmullin.drifter.debug.FPSDisplay
import xyz.jmullin.drifter.extensions.V2
import xyz.jmullin.drifter.extensions.div
import xyz.jmullin.drifter.extensions.game
import xyz.jmullin.drifter.extensions.gameSize
import xyz.jmullin.drifter.sound.Play
import xyz.jmullin.ld38.Stage.Postprocessing
import xyz.jmullin.ld38.Stage.Ui
import xyz.jmullin.ld38.entity.*

class MainScreen : DrifterScreen(Color.BLACK) {

    val world = newLayer2D(1, V2(1024f, 768f), true, Postprocessing) {
        add(MainController())
        add(Background())
        add(Panel())
        add(DockingArm)

        add(Drone)

        Play.music(Assets.noise, 0.3f)
    }

    val ui = newLayer2D(2, V2(1024f, 768f), true, Ui) {
        if(game().devMode) {
            add(FPSDisplay(Assets.prototype14, Color.WHITE, Color.CLEAR, V2(1f, 1f)))
        }
    }
}