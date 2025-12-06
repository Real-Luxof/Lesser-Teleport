package com.luxof.lessertp

import at.petrak.hexcasting.api.casting.mishaps.Mishap

/* I'm too lazy to mixin into the hexcasting mishap catcher.
 * smh.
 */

object MishapThrower {
    fun throwMishap(mishap: Mishap) {
        throw mishap
    }
}
