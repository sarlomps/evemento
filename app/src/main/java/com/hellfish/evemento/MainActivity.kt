package com.hellfish.evemento

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.hellfish.evemento.event.list.EventListFragment
import android.util.Log
import android.view.MenuItem
import com.hellfish.evemento.event.Event
import com.hellfish.evemento.extensions.showSnackbar
import com.hellfish.evemento.extensions.toVisibility

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer.*
import kotlinx.android.synthetic.main.nav_header.view.*
import net.danlew.android.joda.JodaTimeAndroid
import android.arch.lifecycle.ViewModelProviders



class MainActivity : AppCompatActivity(), Navigator {

    lateinit var eventViewModel: EventViewModel
    override var onBackPressedListener: OnBackPressedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawer)

        if (!SessionManager.isLoggedIn) {
            showLoginActivity()
        }

        JodaTimeAndroid.init(this)
        eventViewModel = ViewModelProviders.of(this).get(EventViewModel::class.java)

        updateNavBarHeader()
        navView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            handleNavItemSelected(menuItem)
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
                            "03/06/2018 - 04:20",
                            "03/06/2018 - 04:20",
                            "Mock Location 1"),
                    Event("Mock Title 2",
                            "Mock Description 2",
                            "03/06/2018 - 04:20",
                            "03/06/2018 - 04:20",
                            "Mock Location 2"),
                    Event("Mock Title 3",
                            "Mock Description 3",
                            "03/06/2018 - 04:20",
                            "03/06/2018 - 04:20",
                            "Mock Location 3"),
                    Event("Mock Title 4",
                            "Mock Description 4",
                            "03/06/2018 - 04:20",
                            "03/06/2018 - 04:20",
                            "Mock Location 4"),
                    Event("Mock Title 5",
                            "Mock Description 5",
                            "03/06/2018 - 04:20",
                            "03/06/2018 - 04:20",
                            "Mock Location 5"),
                    Event("Mock Title 6",
                            "Mock Description 6",
                            "03/06/2018 - 04:20",
                            "03/06/2018 - 04:20",
                            "Mock Location 6"))

            args.putParcelableArrayList("events", events)
            fragment.arguments = args
            supportFragmentManager.beginTransaction().add(R.id.main_container, fragment).commit()
        }
    }


    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerOpen(GravityCompat.START) -> drawerLayout.closeDrawer(GravityCompat.START)
            onBackPressedListener != null -> onBackPressedListener?.invoke()
            else -> super.onBackPressed()
        }
    }

    private fun updateNavBarHeader() {
        val headerView = navView.getHeaderView(0)
        headerView.navBarUserName.text = SessionManager.currentUser?.displayName
        headerView.navBarUserEmail.text = SessionManager.currentUser?.email
    }

    private fun handleNavItemSelected(menuItem: MenuItem) {
        if (menuItem.itemId == R.id.nav_logout) {
            SessionManager.logout(this) { success, message ->
                if (success) {
                    showSnackbar(message, main_container)
                    showLoginActivity()
                    finish()
                }
            }
        }
    }

    private fun showLoginActivity() {
        val intent = Intent(applicationContext, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent)
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
        onBackPressedListener = null
        supportFragmentManager.
                beginTransaction().
                replace(R.id.main_container, fragment).
                addToBackStack(null).
                commit()
    }

}