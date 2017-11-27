package xyz.jmullin.ld38.data

import xyz.jmullin.drifter.extensions.probability
import xyz.jmullin.ld38.entity.ship

object Dialogue {
    fun hail() = pick(
        "${greeting().cap()}, ${shipName()}. Welcome to ${port()}."
    )

    fun destination() = pick(
        "${shipName().cap()}, what is your intended course?",
        "${shipName().cap()}, what is your desired course?"
    )

    fun listDestination() = pick(
        "We are bound for ${ship.destination}.${destinationAddendum()}",
        "We intend to continue on to ${ship.destination}.${destinationAddendum()}",
        "Our destination is ${ship.destination}.${destinationAddendum()}"
    )

    fun destinationAddendum() = if(!ship.requirements.contains(NavigationRequirement)) {
        pick(" We have already laid in a course.")
    } else ""

    fun dockApproved() = pick(
        "${shipName().cap()}, you are approved to dock. ${dockRange()}",
        "${shipName().cap()}, you are approved to dock. ${dockRange()}",
        "${shipName().cap()}, you are cleared for docking. ${dockRange()}"
    )
    fun dockRange() = pick(
        "Please move into range.",
        "Please move to approach.",
        "Please begin your approach.",
        "Please approach the station."
    )
    fun dockInitiated() = pick(
        "Moving to approach ${station()}.",
        "Beginning approach.",
        "Beginning approach to ${station()}."
    )
    fun alreadyDocking() = pick(
        "Thank you ${me()}, but I believe we already have clearance.",
        "Thank you ${me()}, we already received your earlier message."
    )

    fun departure() = pick(
        "${shipName().cap()}, you are approved for departure.${if(probability(0.5f)) " " + safeTrip() else ""}",
        "${shipName().cap()}, you are free to leave the station.${if(probability(0.5f)) " " + safeTrip() else ""}",
        "${shipName().cap()}, you are cleared for departure.${if(probability(0.5f)) " " + safeTrip() else ""}"
    )
    fun safeTrip() = pick(
        "Have a safe trip.",
        "We wish you a safe journey.",
        "See you next time."
    )
    fun departureInitiated() = pick(
        "Drives engaged. See you later, ${me()}.",
        "${thanks().cap()} ${me()}, we'll be on our way.",
        "Understood. Departure underway."
    )

    fun departureArmLocked() = pick(
        "Sounds good, ${me()}. We'll leave as soon as the docking arm is disengaged.",
        "Understood. Please disengage the docking arm.",
        "Understood. We are still docked, but will get underway as soon as you release us."
    )
    fun haventDockedYet() = pick(
        "${me().cap()}, we haven't docked yet. We're not going anywhere until we get some fuel.",
        "${me().cap()}, we've only just arrived. Please initiate docking procedures so we can refuel.",
        "${me().cap()}, we would prefer to dock and refuel before departure."
    )

    fun requirements() = pick(
        "How may I be of assistance?",
        "What can I do for you today?",
        "Please relay your instructions.",
        "Please send your requirements, ${shipName()}."
    )

    fun remindMe() = pick(
        "I'm sorry, ${shipName()}, can you ${remindRequestQuestion()}",
        "${shipName().cap()}, could you ${remindRequestQuestion()}",
        "${shipName().cap()}, would you ${remindRequestQuestion()}",
        "${shipName().cap()}, please ${remindRequestStatement()}"
    )

    fun remindRequestQuestion() = pick(
        "remind me what your ${requests()} are?",
        "describe again your ${requests()}?",
        "reiterate your ${requests()}?"
    )

    fun remindRequestStatement() = pick(
        "remind me of your ${requests()}.",
        "describe again your ${requests()}.",
        "reiterate your ${requests()}."
    )

    fun requests() = pick("requests", "open requests", "requirements")

    fun hailResponse() = if(ship.approvedToDock) {
        "${greeting().cap()} ${me()}."
    } else {
        "${greeting().cap()}, ${me()}. ${ship.callsign.cap()} here, requesting docking procedures."
    }

    fun again() = pick(
        "Again?",
        "Have you forgotten already?",
        "If you insist..."
    )

    fun noRequirements() = pick(
        "We are good to go here.",
        "${thanks().cap()}, ${me()}, we are good to go.",
        "Nothing left on our end. Ready to push off at your convenience.",
        "Ready to go over here.",
        "All set, ${me()}, ready to set sail."
    )

    fun listRequirements(requirements: List<Requirement>) = if(requirements.size == 1) pick(
        "${we().cap()} ${require()} ${format(requirements)}.",
        "${we().cap()} ${require()} only ${format(requirements)}."
    ) else pick(
        "${we().cap()} ${require()}: ${format(requirements)}.",
        "${we().cap()} ${require()} the following: ${format(requirements)}."
    )

    fun  missedRequirements(missedRequirements: List<Requirement>) = pick(
        "The ${ship.callsign} reports that you missed ${reqsNum(missedRequirements.size)} during their recent stop. Your record has been penalized accordingly."
    )

    fun reqsNum(n: Int) = if(n == 1) {
        "a requirement"
    } else if (n == 2) {
        pick("two requirements", "multiple requirements")
    } else {
        pick("a few requirements", "a number of requirements", "several requirements")
    }

    fun require() = pick(
        "require",
        "need",
        "are requesting"
    )

    fun format(requirements: List<Requirement>) = list(requirements.map { it.actionNoun })

    fun we() = pick("we")
    fun thanks() = pick("thank you", "thanks")
    fun greeting() = pick("hello", "hello", "hello", "greetings", "good day")
    fun port() = pick("Utility Station OR-152")
    fun station() = pick(port(), "the station")
    fun me() = pick("tower", "controller", "station", "OR-152")
    fun list(things: List<String>): String {
        if(things.size == 1) return things.first()
        if(things.size == 2) return "${things.first()} and ${things.last()}"
        if(things.size > 2) return "${things.dropLast(1).joinToString(", ")}, and ${things.last()}"
        return ""
    }
    fun shipName() = pick(
        ship.callsign
    )

    fun String.cap() = this.capitalize()
}