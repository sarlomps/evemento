package com.hellfish.evemento.extensions

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

// No hace falta pero de paso practico extender clases con kotlin :)
fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes,this,attachToRoot)
}

fun Boolean.toVisibility() = if (this) View.VISIBLE else View.GONE
