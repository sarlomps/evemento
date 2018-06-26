package com.hellfish.evemento

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.hellfish.evemento.extensions.showSnackbar
import kotlinx.android.synthetic.main.activity_sign_in.*


class SignInActivity : AppCompatActivity() {
    private val RC_LOGIN = 888

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sign_in)

        if(SessionManager.isLoggedIn){
            getUserDataAndShowMainActivity()
        } else {
            val loginIntent = SessionManager.getLoginView()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivityForResult(loginIntent, RC_LOGIN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_LOGIN){

            SessionManager.handleLoginResponse(resultCode, data) { success, message ->
                showSnackbar(message, sign_in_container)
                if (success) {
                    getUserDataAndShowMainActivity()
                }
                else {
                    finish()
                }
            }
        }

    }

    private fun getUserDataAndShowMainActivity() {
        // At this point the user must be logged in so we force a non null SessionManager.getCurrentUserId()
        SessionManager.refreshUserIfNecessary { user, errorMessage ->
            user?.let {
                showMainActivity()
                return@refreshUserIfNecessary
            }
            showSnackbar(errorMessage ?: R.string.api_error_fetching_data, sign_in_container)
        }
    }
    private fun showMainActivity() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}
