package com.sarlomps.evemento.event.transport

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sarlomps.evemento.EventViewModel
import com.sarlomps.evemento.NavigatorFragment
import com.sarlomps.evemento.R
import kotlinx.android.synthetic.main.fragment_transport.*
import android.support.v4.content.ContextCompat
import android.support.annotation.DrawableRes
import com.sarlomps.evemento.SessionManager
import com.sarlomps.evemento.api.User


class TransportFragment : NavigatorFragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    override val titleId = R.string.title_activity_maps

    private lateinit var eventViewModel: EventViewModel
    private lateinit var transportViewModel: TransportViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventViewModel = ViewModelProviders.of(activity!!).get(EventViewModel::class.java)
        eventViewModel.loadRides { _ -> showToast(R.string.errorLoadingRides) }
        eventViewModel.rides.observe(this, Observer { if (::mMap.isInitialized) it?.let { loadTransportsOnMap(it) } })

        transportViewModel = ViewModelProviders.of(activity!!).get(TransportViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_transport, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cardView.setOnClickListener { navigatorListener.replaceFragment(TransportListFragment()) }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    private fun loadTransportsOnMap(transports: MutableList<TransportItem>) {
        mMap.clear()
        transports.forEach {
            val marker = MarkerOptions()
                    .position(it.latLong())
                    .title(it.startpoint.name)
                    .icon(bitmapDescriptorFromVector(context!!, R.drawable.ic_map_car_white_30dp))

            mMap.addMarker(marker)

        }
        eventViewModel.selected()!!.location.let {
            val marker = MarkerOptions()
                    .position(it.latLng())
                    .title(it.name)
            mMap.addMarker(marker)

            val currentLocation = it.latLng()
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))

        }

        mMap.setOnMarkerClickListener { marker ->
            //TODO si hay varios markers en la misma localizacion va a ir al que encuentre el find que puede no ser el correcto
            val transportClicked = transports.find { it.latLong().equals(marker.position) }
            if (transportClicked != null) {
                transportViewModel.selectDriver(transportClicked.driver)
                navigatorListener.replaceFragment(TransportDetailFragment())
            }
            return@setOnMarkerClickListener false
        }
    }

    private fun getLastLocation() = LatLng(2.0, 2.0)

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        eventViewModel.rides.observe(this, Observer { it?.let { loadTransportsOnMap(it) } })
    }

    private fun bitmapDescriptorFromVector(context: Context, @DrawableRes vectorDrawableResourceId: Int): BitmapDescriptor {
        val background = ContextCompat.getDrawable(context, R.drawable.ic_place_primary_blue_48dp)
        background!!.setBounds(0, 0, background.intrinsicWidth, background.intrinsicHeight)
        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        vectorDrawable!!.setBounds(25, 3, vectorDrawable.intrinsicWidth + 20, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(background.intrinsicWidth, background.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        background.draw(canvas)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
