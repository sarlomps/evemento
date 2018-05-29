package com.hellfish.evemento

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import com.hellfish.evemento.event.Event
import com.hellfish.evemento.event.EventFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Navigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val fragment = EventFragment()
            val args = Bundle()
            args.putParcelable("event", Event("Mock Title",
                    "Mock Description",
                    "Mock Time",
                    "Mock Location",
                    listOf("Juan", "Juan", "Juan"),
                    listOf("rides"),
                    listOf("tasks"),
                    listOf("polls"),
                    listOf("comments")))
            fragment.arguments= args
            supportFragmentManager.beginTransaction().add(R.id.main_container, fragment).commit()
        }
    }

    override fun setCustomToolbar(customToolbar: Toolbar?, displayTitle: Boolean) {
        defaultToolbar.visibility= if (customToolbar == null) View.VISIBLE else View.GONE
        setSupportActionBar(customToolbar ?: defaultToolbar)
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
