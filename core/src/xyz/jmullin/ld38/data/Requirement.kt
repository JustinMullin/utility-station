package xyz.jmullin.ld38.data

import xyz.jmullin.ld38.entity.screen.Navigation
import xyz.jmullin.ld38.entity.ship

abstract class Requirement(val actionNoun: String) {
    abstract fun fulfilled(): Boolean
}

object DecontaminationRequirement : Requirement("decontamination") {
    override fun fulfilled() = ship.contaminations.isEmpty()
}

object RepairRequirement : Requirement("repairs") {
    override fun fulfilled() = ship.damages.isEmpty()
}

object RefuelRequirement : Requirement("refueling") {
    override fun fulfilled() = ship.tankLevel.zip(ship.maxTankLevel).all { a -> a.first >= a.second-0.1f }
}

object ResupplyRequirement : Requirement("resupply") {
    override fun fulfilled() = ship.supply.zip(ship.maxSupply).all { a -> a.first >= a.second-0.1f }
}

object NavigationRequirement : Requirement("course calculation") {
    override fun fulfilled() = ship.course == ship.destination
}