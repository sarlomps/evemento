package com.hellfish.evemento.event.guest

import android.graphics.Color
import kotlin.math.abs

interface UserColor {
    fun userColor(userId: String, name: String): Int {
        val red = abs(userId.hashCode() % 256)
        val green = abs(name.hashCode() % 256)
        val blue = abs((userId + name).hashCode() % 256)
        return Color.argb(255, red, green, blue)
    }
}