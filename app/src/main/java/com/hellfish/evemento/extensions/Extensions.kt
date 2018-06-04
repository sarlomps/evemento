package com.hellfish.evemento.extensions

import android.support.annotation.LayoutRes
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellfish.evemento.R

// No hace falta pero de paso practico extender clases con kotlin :)
fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes,this,attachToRoot)
}

fun Boolean.toVisibility() = if (this) View.VISIBLE else View.GONE

fun showSnackbar(message : String, view: View, length:Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(view, message, length).show()
}
fun showSnackbar(id : Int, view:View, length:Int = Snackbar.LENGTH_LONG) {
    showSnackbar(view.resources.getString(id), view, length)
}
