package com.hellfish.evemento.event.guest

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.hellfish.evemento.*
import com.hellfish.evemento.R.string.title_fragment_guests_list
import com.hellfish.evemento.api.User
import kotlinx.android.synthetic.main.fragment_guest_list.*

class GuestListFragment : NavigatorFragment() {

    override val titleId = title_fragment_guests_list

    lateinit var eventViewModel: EventViewModel
    lateinit var dialogInput: EditText
    lateinit var clickDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventViewModel = ViewModelProviders.of(activity!!).get(EventViewModel::class.java)
        eventViewModel.loadGuests { _ -> showToast(R.string.errorLoadingComments) }
        eventViewModel.guests.observe(this, Observer { guests ->
            guests?.let { guestsRecyclerView.adapter = GuestAdapter(it.toMutableList(), editListener()) }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialogInput = EditText(activity)
        clickDialog = createAlertDialog(R.string.guestDialogTitle,
                dialogInput,
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
            getButton(Dialog.BUTTON_POSITIVE).let {
                it.text = getString(R.string.accept)
                it.setOnClickListener {
                    //TODO Chequear contra firebase a ver si lo ingresado existe fetcheando ese usuario
                    val newGuest = User("traido de firebase",
                            "traido de firebase",
                            "traido de firebase",
                            "traido de firebase")

                    eventViewModel.add(newGuest)
                    cancel()
                }
            }
        }

    }

    private fun editListener() = { guest: User ->
        View.OnClickListener {
            if (eventViewModel.selected()?.user == SessionManager.getCurrentUser()!!.userId) {
                dialogInput.isEnabled = false
                dialogInput.setText(String.format("%s (%s)", guest.email, guest.displayName))
                clickDialog.run {
                    show()
                    getButton(Dialog.BUTTON_POSITIVE).let {
                        it.text = getString(R.string.delete)
                        it.setOnClickListener { eventViewModel.remove(guest); cancel() }
                    }
                }
            }
        }
    }

}

