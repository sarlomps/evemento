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
import org.joda.time.DateTime


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
                            "https://fsmedia.imgix.net/43/e9/9e/ac/0c64/4d0e/a2bc/dda1d61a31db/on-fire.jpeg?rect=0%2C56%2C1280%2C640&auto=format%2Ccompress&w=650",
                            "Mock Description 1",
                            DateTime(2018, 6, 3, 4, 20),
                            DateTime(2018, 6, 3, 5, 20),
                            "Mock Location 1"),
                    Event("Mock Title 2",
                            "",
                            "Mock Description 2",
                            DateTime(2018, 6, 3, 4, 20),
                            DateTime(2018, 6, 3, 5, 20),
                            "Mock Location 2"),
                    Event("Mock Title 3",
                            "https://user-images.githubusercontent.com/11724540/41494532-7a993580-70ea-11e8-89de-bafb040bc1f5.png",
                            "Mock Description 3",
                            DateTime(2018, 6, 3, 4, 20),
                            DateTime(2018, 6, 3, 5, 20),
                            "Mock Location 3"),
                    Event("Mock Title 4",
                            "",
                            "Mock Description 4",
                            DateTime(2018, 6, 3, 4, 20),
                            DateTime(2018, 6, 3, 5, 20),
                            "Mock Location 4"),
                    Event("Mock Title 5",
                            "https://camo.githubusercontent.com/3ecb93746cdbebe755c1f8b35471037c243aea3b/68747470733a2f2f322e62702e626c6f6773706f742e636f6d2f2d593551426f5065714872492f576544485f626c4a7036492f41414141414141414463732f674333785f6f6d50426d734c576f705670646e4367456f7953362d384d644b6a67434c63424741732f73313630302f6d656d656e746f2d73616d6d792e6a7067",
                            "Mock Description 5",
                            DateTime(2018, 6, 3, 4, 20),
                            DateTime(2018, 6, 3, 5, 20),
                            "Mock Location 5"),
                    Event("Mock Title 6",
                            "",
                            "Mock Description 6",
                            DateTime(2018, 6, 3, 4, 20),
                            DateTime(2018, 6, 3, 5, 20),
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