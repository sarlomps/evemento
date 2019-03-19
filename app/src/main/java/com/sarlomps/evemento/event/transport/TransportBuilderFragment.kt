package com.sarlomps.evemento.event.transport

import android.app.Activity
import android.arch.lifecycle.Observer
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
import com.google.android.gms.maps.model.LatLng
import com.sarlomps.evemento.*


class TransportBuilderFragment : NavigatorFragment() {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var transportViewModel: TransportViewModel
    private lateinit var coordinates: LatLng

    private val autocompleteRequestCode = 42

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventViewModel = ViewModelProviders.of(activity!!).get(EventViewModel::class.java)
        transportViewModel = ViewModelProviders.of(activity!!).get(TransportViewModel::class.java)

        transportViewModel.transport.observe(this, Observer { transport ->
            transport?.let{
                transport_builder_slots.setText(transport.totalSlots.toString())
                transport_builder_location.setText(transport.startpoint.name)
                this.coordinates = transport.latLong()
                transport_builder_fab.setOnClickListener {
                    if (validateTransport()) {
                        eventViewModel.edit(transport(transport.transportId)) { _, errorMessage ->
                            if (errorMessage == null) {
                                transport_builder_fab?.run {
                                    setOnClickListener(null)
                                    transportViewModel.selectDriver(transport.driver)
                                    navigatorListener.popBackstack()
                                }
                            } else showToast(errorMessage)
                        }
                    }
                }
                delete_transport.visibility = ViewGroup.VISIBLE
                delete_transport.setOnClickListener { showDialogConfirmation(transport) }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_transport_builder, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLocationListener()
        setSavedListener()
    }

    private fun showDialogConfirmation(transport: TransportItem) {
        AlertDialog.Builder(view!!.context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Deleting transport")
                .setMessage("Are you sure you want to delete this transport?")
                .setPositiveButton("Delete") { _, _ ->
                    activity!!.supportFragmentManager.popBackStack()
                    activity!!.onBackPressed()
                    eventViewModel.remove(transport)
                }
                .setNegativeButton("Cancel", null)
                .show()
    }

    private fun setSavedListener() {
        transport_builder_fab.setOnClickListener {
            if (validateTransport()) {
                eventViewModel.add(transport(""))
                navigatorListener.popBackstack()
            }
        }
    }

    private fun transport(transportId: String): TransportItem {
        val locationName = transport_builder_location.text.toString()
        val totalSlots = transport_builder_slots.text.toString().toInt()
        return TransportItem(transportId, SessionManager.getCurrentUser()!!, ArrayList(), Location(locationName, Coordinates(this.coordinates)), totalSlots)
    }

    private fun validateTransport(): Boolean {
        val locationEmpty = transport_builder_location.text.toString().isEmpty()
        val totalSlotsEmpty = transport_builder_slots.text.toString().isEmpty()
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
