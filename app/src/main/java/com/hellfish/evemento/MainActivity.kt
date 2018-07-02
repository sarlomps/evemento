package com.hellfish.evemento

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.hellfish.evemento.event.list.EventListFragment
import android.util.Log
import android.view.MenuItem
import com.hellfish.evemento.extensions.showSnackbar
import com.hellfish.evemento.extensions.toVisibility
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer.*
import kotlinx.android.synthetic.main.nav_header.view.*
import net.danlew.android.joda.JodaTimeAndroid
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

class MainActivity : AppCompatActivity(), Navigator {

    lateinit var eventViewModel: EventViewModel
    override var onBackPressedListener: OnBackPressedListener? = null
    private val permissionsRequestCode = 101

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
        setupPermissions()
        if (savedInstanceState == null) {
            val fragment = EventListFragment()
            // Cargo el EventListFragment sin pasarle argumento porque la llamada para pedir eventos la hace despues.
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
        headerView.navBarUserName.text = SessionManager.getCurrentUser()?.displayName
        headerView.navBarUserEmail.text = SessionManager.getCurrentUser()?.email
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

    override fun setCustomToolbar(customToolbar: Toolbar?, title: String?, homeEnabled: Boolean) {
        defaultToolbar.visibility= (customToolbar == null).toVisibility()
        setSupportActionBar(customToolbar ?: defaultToolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(homeEnabled)
            setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        }
        supportActionBar?.title = title
    }

    override fun replaceFragment(fragment: Fragment, addToBackStack: Boolean) {
        onBackPressedListener = null
        val transaction = supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_container, fragment)

        if (addToBackStack) transaction.addToBackStack(null)

        transaction.commit()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode != permissionsRequestCode) return
        if (isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Success case: Get the permission
            // Do something and return
            return
        }


    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {

            makeRequest()
        }

    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                permissionsRequestCode)

    }

    fun isPermissionGranted(permission:String):Boolean =
            ContextCompat.checkSelfPermission(
                    this,
                    permission
            ) == PackageManager.PERMISSION_GRANTED

}