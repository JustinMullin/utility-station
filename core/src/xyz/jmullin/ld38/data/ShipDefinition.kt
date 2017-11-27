package xyz.jmullin.ld38.data

import com.badlogic.gdx.math.Vector2
import xyz.jmullin.drifter.animation.delay
import xyz.jmullin.drifter.extensions.probability
import xyz.jmullin.drifter.extensions.rElement
import xyz.jmullin.drifter.extensions.rFloat
import xyz.jmullin.drifter.extensions.rInt
import xyz.jmullin.ld38.entity.screen.Comms
import xyz.jmullin.ld38.entity.screen.Navigation
import xyz.jmullin.ld38.entity.screen.Performance
import xyz.jmullin.ld38.entity.screen.Provisions

class ShipDefinition {
    val makeIndex = rInt(0, 3)

    val callsign =
        (0 until rInt(2, 4)).map { randomCharacter() }.joinToString("") +
            "-" + (0 until rInt(2, 4)).map { randomCharacter() }.joinToString("")
    fun randomCharacter() = rElement("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toList())
    val designation = "human vessel"
    val make = "Starship"

    val requirements = listOf(
        if(probability(0.3f)) DecontaminationRequirement else null,
        if(probability(0.3f)) RepairRequirement else null,
        if(probability(0.3f)) NavigationRequirement else null,
        if(probability(0.7f)) ResupplyRequirement else null,
        RefuelRequirement
    ).filterNotNull()

    fun remainingRequirements() = requirements

    fun demeritsForMissedRequirements() {
        val missedRequirements = requirements.filterNot { it.fulfilled() }
        if(missedRequirements.isNotEmpty()) {
            Performance.demerits += missedRequirements.size
            Performance.log(Dialogue.missedRequirements(missedRequirements))
        }
    }

    var course = ""
    val destination = rElement(Navigation.stars).name

    val startTime = System.currentTimeMillis()

    val tanks = rInt(2, 4)
    val maxTankLevel = (0 until tanks).map { rFloat(10f, 20f) }
    val tankLevel = maxTankLevel.map { rFloat(0f, it) }.toMutableList()
    val maxSupply = (0 until Provisions.supplyNames.size).map { it ->
        val low = rFloat(10f-it*3f, 15f-it*3f)
        rFloat(low, low + rFloat(1f, 10f))
    }
    val supply = maxSupply.map { rFloat(it/3f, it) }.toMutableList()

    fun waitTime(): Long {
        val time = System.currentTimeMillis()
        return (time - startTime)/1000
    }

    fun formatWaitTime(): String {
        val duration = waitTime()
        return String.format("%d:%02d", duration / 60, duration % 60)
    }

    data class Contamination(val v: Vector2) {
        var strength: Float = rFloat(1f, 3f)
    }

    data class Damage(val v: Vector2) {
        var strength: Float = rFloat(1f, 3f)
    }

    var crew = listOf<Vector2>()
    var contaminations = listOf<Contamination>()
    var damages = listOf<Damage>()

    fun addCrew(v: Vector2) {
        crew += v
    }

    fun addContamination(v: Vector2) {
        contaminations += Contamination(v)
    }

    fun addDamage(v: Vector2) {
        damages += Damage(v)
    }

    var timeWarnings = 0

    var commsOpen = false
    var statusTimes = 0
    var timesHailed = 0
    var approvedToDock = false
    var approvedToLeave = false
    var docked = false

    fun requests() {
        if(statusTimes < 2) {
            listRequirements()
        } else {
            Comms.incoming(Dialogue.again()) then delay(0.5f) {
                listRequirements()
            }
        }
        statusTimes += 1
    }

    fun hail() {
        if(timesHailed < 2) {
            Comms.incoming(Dialogue.hailResponse())
        } else {
            Comms.incoming(Dialogue.again())
        }

        timesHailed += 1
    }

    fun listRequirements() {
        if(remainingRequirements().isEmpty()) {
            Comms.incoming(Dialogue.noRequirements())
        } else {
            Comms.incoming(Dialogue.listRequirements(requirements))
        }
    }
}