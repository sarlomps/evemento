package com.hellfish.evemento.event.guest

import android.graphics.Color
import kotlin.math.abs

interface CircleColor {
    fun circleColor(first: String, second: String): Int {
        val red = abs(first.hashCode() % 256)
        val green = abs(second.hashCode() % 256)
        val blue = abs((first + second).hashCode() % 256)
        return Color.argb(255, red, green, blue)
    }
}