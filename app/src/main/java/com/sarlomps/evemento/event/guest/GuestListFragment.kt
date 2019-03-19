package com.sarlomps.evemento.event.guest

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sarlomps.evemento.*
import com.sarlomps.evemento.R.string.title_fragment_guests_list
import com.sarlomps.evemento.api.Guest
import com.sarlomps.evemento.api.User
import kotlinx.android.synthetic.main.fragment_guest_list.*

class GuestListFragment : NavigatorFragment() {

    override val titleId = title_fragment_guests_list

    private lateinit var eventViewModel: EventViewModel
    private lateinit var dialogInputLayout: TextInputLayout
    private lateinit var dialogInput: TextInputEditText
    private lateinit var clickDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventViewModel = ViewModelProviders.of(activity!!).get(EventViewModel::class.java)
        eventViewModel.loadGuests { _ -> showToast(R.string.errorLoadingGuests) }
        eventViewModel.guests.observe(this, Observer { guests ->
            guests?.let { guestsRecyclerView.adapter = GuestAdapter(it.toMutableList(), deleteListener()) }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialogInput = TextInputEditText(activity)
        dialogInputLayout = TextInputLayout(activity).apply { addView(dialogInput) }
        clickDialog = createAlertDialog(R.string.guestDialogTitle,
                dialogInputLayout,
                Pair(R.string.cancel, { _, _ -> Unit }),
                Pair(R.string.cancel, { _, _ -> Unit }))
        return inflater.inflate(R.layout.fragment_guest_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        guestListFab.setOnClickListener(addListener())
        guestsRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun addListener() = View.OnClickListener {
        dialogInput.isEnabled = true
        dialogInput.setText("")
        clickDialog.run {
            show()
            dialogInputLayout.error = null
            getButton(Dialog.BUTTON_POSITIVE).let {
                it.text = getString(R.string.invite)
                it.setOnClickListener {
                    val inputEmail = dialogInput.text.toString()
                    if (inputEmail != SessionManager.getCurrentUser()?.email) {
                        if (eventViewModel.guests.value!!.all { it.email != inputEmail}) {
                            NetworkManager.getAllUsers { users, errorMessage ->
                                if (users != null) addNewGuest(users.find { it.email == inputEmail })
                                else { showToast(errorMessage ?: R.string.network_unknown_error); cancel() }
                            }
                        } else dialogInputLayout.error = getString(R.string.cannotInviteGuest)
                    } else dialogInputLayout.error = getString(R.string.youAlreadyInvited)
                }
            }
        }

    }

    private fun addNewGuest(user: User?) {
        if (user != null) {
            val newGuest = Guest("", user.userId, user.displayName, user.imageUrl, user.email)
            NetworkManager.pushGuest(eventViewModel.selected()!!.eventId, newGuest) { id, errorMessage ->
                if (id != null) eventViewModel.add(newGuest.copy(guestId = id))
                else showToast(errorMessage ?: R.string.network_unknown_error)
                dialogInputLayout.error = null
                clickDialog.cancel()
            }
        }
        else dialogInputLayout.error = getString(R.string.usertMustHaveAccount)
    }

    private fun deleteListener() = { guest: Guest ->
        View.OnClickListener {
            if (eventViewModel.selected()?.user == SessionManager.getCurrentUser()!!.userId) {
                dialogInput.isEnabled = false
                dialogInput.setText(String.format("%s (%s)", guest.email, guest.displayName))
                clickDialog.run {
                    show()
                    dialogInputLayout.error = null
                    getButton(Dialog.BUTTON_POSITIVE).let {
                        it.text = getString(R.string.uninvite)
                        it.setOnClickListener {
                            withConfirmationDialog(R.string.deleteGuestConfirmation) {
                                NetworkManager.deleteGuest(guest) { success, errorMessage ->
                                    if (success) eventViewModel.remove(guest)
                                    else showToast(errorMessage ?: R.string.network_unknown_error)
                                }
                                cancel()
                            }
                        }
                    }
                }
            }
        }
    }

}

