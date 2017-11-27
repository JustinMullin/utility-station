package xyz.jmullin.ld38.data

import xyz.jmullin.drifter.extensions.rElement

fun pick(vararg option: String) = rElement(option.toList())