package com.hellfish.evemento

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.hellfish.evemento.event.EventDetailFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Navigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val fragment = EventDetailFragment()
            supportFragmentManager.beginTransaction().add(R.id.main_container, fragment).commit()
        }
    }

    override fun setCustomToolbar(customToolbar: Toolbar, displayTitle: Boolean) {
        setSupportActionBar(customToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(displayTitle)
    }

    override fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.
                beginTransaction().
                replace(R.id.main_container, fragment).
                addToBackStack(null).
                commit()
    }

}
