package com.hellfish.evemento

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.hellfish.evemento.event.CustomToolbar
import com.hellfish.evemento.event.EventDetailFragment

class MainActivity : AppCompatActivity(), CustomToolbar  {

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

}
