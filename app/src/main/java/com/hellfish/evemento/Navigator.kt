package com.hellfish.evemento

import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar

interface Navigator {

    var onBackPressedListener: OnBackPressedListener?

    fun setCustomToolbar(customToolbar: Toolbar? = null, title: String? = null, homeEnabled: Boolean = true)
    fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = true)

}

typealias OnBackPressedListener = () -> Unit