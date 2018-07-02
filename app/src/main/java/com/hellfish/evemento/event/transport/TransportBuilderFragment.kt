package com.hellfish.evemento.event.transport

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import kotlinx.android.synthetic.main.fragment_transport_builder.*
import android.support.v7.app.AlertDialog
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.hellfish.evemento.*
import com.hellfish.evemento.api.User


class TransportBuilderFragment : NavigatorFragment() {
    private lateinit var loggedInUser: User
    private lateinit var eventViewModel: EventViewModel
    private lateinit var coordinates: LatLng

    private val autocompleteRequestCode = 42

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventViewModel = ViewModelProviders.of(activity!!).get(EventViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_transport_builder, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loggedInUser = SessionManager.getCurrentUser()!!
        setLocationListener()
        setSavedListener()

        val argTransport = arguments?.getParcelable<TransportItem>("transport")

        if (argTransport != null) {
            transport_builder_slots.setText(argTransport.totalSlots.toString())
            transport_builder_location.setText(argTransport.startpoint.name)
            transport_builder_fab.setOnClickListener {
                if (validateTransport()) {
                    NetworkManager.updateTransport(eventViewModel.selected()!!.eventId, transport()) { transport, errorMessage ->
                        transport?.let {
                            eventViewModel.edit(transport)
                            navigatorListener.replaceFragment(TransportListFragment())
                            return@updateTransport
                        }
                        showToast(errorMessage ?: R.string.api_error_fetching_data)
                    }
                }
            }
            delete_transport.visibility = ViewGroup.VISIBLE
            delete_transport.setOnClickListener {
                showDialogConfirmation(argTransport)
            }
        }
    }

    private fun showDialogConfirmation(transport: TransportItem) {
        AlertDialog.Builder(view!!.context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Deleting transport")
                .setMessage("Are you sure you want to delete this transport?")
                .setPositiveButton("Delete", { _, _ ->
                    eventViewModel.remove(transport)
                    navigatorListener.replaceFragment(TransportListFragment())
                })
                .setNegativeButton("Cancel", null)
                .show()
    }

    private fun setSavedListener() {
        transport_builder_fab.setOnClickListener {
            if (validateTransport()) {
                eventViewModel.add(transport())
                navigatorListener.replaceFragment(TransportListFragment())
            }
        }
    }

    private fun transport(): TransportItem {
        var locationName = transport_builder_location.text.toString()
        var totalSlots = transport_builder_slots.text.toString().toInt()
        return TransportItem("",loggedInUser, ArrayList(), Location(locationName, Coordinates(this.coordinates)), totalSlots)
    }

    private fun validateTransport(): Boolean {
        var locationEmpty = transport_builder_location.text.toString().isEmpty()
        var totalSlotsEmpty = transport_builder_slots.text.toString().isEmpty()
        if (totalSlotsEmpty) showToast(R.string.transport_build_total_slots_validation)
        if (locationEmpty) showToast(R.string.transport_build_location_validation)
        return !locationEmpty && !totalSlotsEmpty
    }

    private fun setLocationListener() = transport_builder_location.setOnClickListener {
        try {
            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(activity)
            startActivityForResult(intent, autocompleteRequestCode)
        } catch (e: Exception) {
            when (e) {
                is GooglePlayServicesRepairableException, is GooglePlayServicesNotAvailableException -> showToast(R.string.autocompleteError)
                else -> throw e
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            requestCode == autocompleteRequestCode && resultCode == Activity.RESULT_OK -> {
                        val gmapPlace = PlaceAutocomplete.getPlace(activity, data)
                        transport_builder_location.setText(gmapPlace.name)
                        this.coordinates = gmapPlace.latLng

                }
            requestCode == autocompleteRequestCode && resultCode == PlaceAutocomplete.RESULT_ERROR -> showToast(R.string.autocompleteError)
            else -> Unit
        }
    }

}
