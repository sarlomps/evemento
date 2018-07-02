package com.hellfish.evemento.extensions

import android.app.Activity
import android.content.Context
import android.support.annotation.LayoutRes
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout

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

fun FloatingActionButton.withDrawable(drawableId: Int): FloatingActionButton =
        apply { setImageDrawable(ResourcesCompat.getDrawable(resources, drawableId, null)) }

fun<ViewType> LinearLayout.getChildren() : List<ViewType> {
    val children = mutableListOf<ViewType>()
    for (i in 0 until childCount) {
        children.add(getChildAt(i) as ViewType)
    }
    return children.toList()
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(findViewById<View>(android.R.id.content).windowToken, 0);
}