package xyz.jmullin.ld38

import xyz.jmullin.drifter.assets.DrifterAssets
import xyz.jmullin.drifter.extensions.rElement

@Suppress("UNUSED")
object Assets : DrifterAssets("ld38") {
    val visitor18 by font

    val prototype14 by font
    val prototype18 by font

    val steelfish20 by font
    val steelfish24 by font
    val steelfish28 by font
    val steelfish48 by font

    val stars by texture
    val starSelector by sprite

    val type1 by sound
    val type2 by sound
    val type3 by sound
    val type4 by sound

    fun randomType() = rElement(listOf(type1, type2, type3, type4))

    val recall by sound("computer1")
    val request by sound("computer2")
    val hello by sound("computer3")
    val no by sound("computer4")
    val hmm by sound("computer5")
    val chirp by sound("computer6")
    val compute by sound("computer7")
    val uhuh by sound("computer8")
    val computer9 by sound("computer9")
    val transmit by sound("computer10")
    val computer11 by sound("computer11")
    val query by sound("computer12")
    val computer13 by sound("computer13")
    val accepted by sound("computer14")
    val calculated by sound("computer15")

    val tractorLoop by sound("tractor")
    val armLoop by sound("armMove")
    val fuelUpLoop by sound("fuelUp")

    val noise by music

    val scan by sound

    val star1 by sprite

    val topper by sprite
    val grabber by animation

    val circle by sprite
    val damage by sprite
    val fuelArrow by sprite

    val reticle by sprite

    val tractorBeam by sprite
    val droneBeam by sprite
    val droneTargeting by sprite

    val ship1Pixmap by pixmap("ship1scan")
    val ship2Pixmap by pixmap("ship2scan")
    val ship3Pixmap by pixmap("ship3scan")

    val ship1 by sprite
    val ship2 by sprite
    val ship3 by sprite

    val lock by sound

    val drone by animation

    val ship1Scan by sprite("ship1scan")
    val ship2Scan by sprite("ship2scan")
    val ship3Scan by sprite("ship3scan")

    val scanBeam by sprite

    val bioIcon by sprite
    val personIcon by sprite

    val console by sprite("botPanel")
    val leftPanel by sprite
    val midPanel by sprite
    val rightPanel by sprite

    val type by sound
}