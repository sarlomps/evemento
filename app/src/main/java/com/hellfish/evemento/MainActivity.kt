package com.hellfish.evemento

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.hellfish.evemento.event.Event
import com.hellfish.evemento.event.list.EventListFragment
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.hellfish.evemento.event.EventTime
import com.hellfish.evemento.extensions.toVisibility

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer.*

class MainActivity : AppCompatActivity(), Navigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawer)

        navView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            drawerLayout.closeDrawers()

            Log.d("Selected MenuItem", menuItem.toString())
            true
        }

        if (savedInstanceState == null) {
            val fragment = EventListFragment()
            val args = Bundle()

            // TODO: Cargar de algun lado sin hardcodear...
            val events = arrayListOf(
                    Event("Mock Title 1",
                            "Mock Description 1",
                            EventTime("Mock Start Date 1", "Mock Start Date 1", "h1:m1", "H1:M1"),
                            "Mock Location 1",
                            listOf("Juan", "Juan", "Juan"),
                            listOf("rides"),
                            listOf("tasks"),
                            listOf("polls"),
                            listOf("comments")),
                    Event("Mock Title 2",
                            "Mock Description 2",
                            EventTime("Mock Start Date 2", "Mock Start Date 2", "h2:m2", "H2:M2"),
                            "Mock Location 2",
                            listOf("Juan", "Juan", "Juan"),
                            listOf("rides"),
                            listOf("tasks"),
                            listOf("polls"),
                            listOf("comments")),
                    Event("Mock Title 3",
                            "Mock Description 3",
                            EventTime("Mock Start Date 3", "Mock Start Date 3", "h3:m3", "H3:M3"),
                            "Mock Location 3",
                            listOf("Juan", "Juan", "Juan"),
                            listOf("rides"),
                            listOf("tasks"),
                            listOf("polls"),
                            listOf("comments")),
                    Event("Mock Title 4",
                            "Mock Description 4",
                            EventTime("Mock Start Date 4", "Mock Start Date 4", "h4:m4", "H4:M4"),
                            "Mock Location 4",
                            listOf("Juan", "Juan", "Juan"),
                            listOf("rides"),
                            listOf("tasks"),
                            listOf("polls"),
                            listOf("comments")),
                    Event("Mock Title 5",
                            "Mock Description 5",
                            EventTime("Mock Start Date 5", "Mock Start Date 5", "h5:m5", "H5:M5"),
                            "Mock Location 5",
                            listOf("Juan", "Juan", "Juan"),
                            listOf("rides"),
                            listOf("tasks"),
                            listOf("polls"),
                            listOf("comments")),
                    Event("Mock Title 6",
                            "Mock Description 6",
                            EventTime("Mock Start Date 6", "Mock Start Date 6", "h6:m6", "H6:M6"),
                            "Mock Location 6",
                            listOf("Juan", "Juan", "Juan"),
                            listOf("rides"),
                            listOf("tasks"),
                            listOf("polls"),
                            listOf("comments")))

            args.putParcelableArrayList("events", events)
            fragment.arguments = args
            supportFragmentManager.beginTransaction().add(R.id.main_container, fragment).commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun setCustomToolbar(customToolbar: Toolbar?, title: String?) {
        defaultToolbar.visibility= (customToolbar == null).toVisibility()
        setSupportActionBar(customToolbar ?: defaultToolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        }
        supportActionBar?.title = title
    }

    override fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.
                beginTransaction().
                replace(R.id.main_container, fragment).
                addToBackStack(null).
                commit()
    }

}
