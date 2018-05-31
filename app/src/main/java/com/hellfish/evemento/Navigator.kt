package com.hellfish.evemento

import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar

interface Navigator {
    fun setCustomToolbar(customToolbar: Toolbar?, title: String?)
    fun replaceFragment(fragment: Fragment)
}