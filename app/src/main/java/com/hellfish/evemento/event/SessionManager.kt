package com.hellfish.evemento.event

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hellfish.evemento.R
import java.util.*

object SessionManager {
    private var fbAuth = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser?
        get() = fbAuth.currentUser

    val isLoggedIn: Boolean
        get() = this.currentUser != null

    fun getLoginView(): Intent {
        val providers = Arrays.asList(AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.PhoneBuilder().build(),
                AuthUI.IdpConfig.EmailBuilder().build())

        return AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()
    }

    fun logout(context: Context, callback: (Boolean, Int) -> (Unit)) {
        AuthUI.getInstance()
                .signOut(context)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(true, R.string.auth_logged_out)
                    } else {
                        callback(false, R.string.auth_error_not_logged_out)
                    }
                }

    }

    fun handleLoginResponse(resultCode: Int, data: Intent?, callback: (Boolean, Int) -> (Unit)) {
        val response = IdpResponse.fromResultIntent(data)
        if(resultCode == Activity.RESULT_OK){
            callback(true, R.string.auth_logged_in)
            return
        }
        else {
            if(response == null){
                //If no response from the Server
                callback(false, R.string.auth_login_cancelled)
                return
            }
            if(response.error?.errorCode == ErrorCodes.NO_NETWORK){
                callback(false, R.string.auth_no_internet_connection)
                return
            }
            if(response.error?.errorCode == ErrorCodes.UNKNOWN_ERROR){
                //If the error cause was unknown
                callback(false, R.string.auth_unknown_login_response)
                return
            }
        }
        callback(false, R.string.auth_unknown_login_response) //if the sign in response was unknown
    }
}
