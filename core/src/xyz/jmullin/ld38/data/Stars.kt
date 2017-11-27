package xyz.jmullin.ld38.data

import xyz.jmullin.drifter.extensions.rElement
import xyz.jmullin.drifter.extensions.rInt

object Stars {
    //val stars =

    fun randomName() =
        (0 until rInt(2, 4)).map { randomCharacter() }.joinToString("") +
            "-" + (0 until rInt(2, 4)).map { randomCharacter() }.joinToString("")
    fun randomCharacter() = rElement("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toList())
}