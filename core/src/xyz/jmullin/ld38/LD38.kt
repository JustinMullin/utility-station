package xyz.jmullin.ld38

import xyz.jmullin.drifter.application.DrifterGame

object LD38 : DrifterGame("ld38", Assets) {
    override fun create() {
        super.create()

        setScreen(MainScreen())
    }
}
