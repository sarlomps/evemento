package com.sarlomps.evemento.event.transport

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sarlomps.evemento.EventViewModel
import com.sarlomps.evemento.NavigatorFragment
import com.sarlomps.evemento.R
import com.sarlomps.evemento.SessionManager
import com.sarlomps.evemento.api.User
import com.sarlomps.evemento.event.guest.CircleColor
import com.sarlomps.evemento.extensions.withDrawable
import kotlinx.android.synthetic.main.fragment_transport_detail.*
import kotlinx.android.synthetic.main.fragment_transport_detail.view.*

class TransportDetailFragment() : NavigatorFragment(), CircleColor {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var transportViewModel: TransportViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventViewModel = ViewModelProviders.of(activity!!).get(EventViewModel::class.java)

        transportViewModel = ViewModelProviders.of(activity!!).get(TransportViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_transport_detail, container, false)


    private fun drawDriverCircle(item: User) {
        DrawableCompat.setTint(driverCircle.drawable, circleColor(item.userId, item.displayName))
        driverInitial.text = item.displayName.first().toUpperCase().toString()
    }


    private fun addPassanger(view: View, passanger: User) {
        val passagerView = createPassangerView(view.context, passanger)
        view.llTransportDetail.addView(passagerView)
    }

    private fun createPassangerView(context: Context, passanger: User): View {
        val linearLayout = layoutInflater.inflate(R.layout.transport_detail_passanger, null)

        linearLayout.findViewById<TextView>(R.id.txtPassangerName).setText(passanger.displayName)
        DrawableCompat.setTint(linearLayout.findViewById<ImageView>(R.id.driverCircle).drawable,
                circleColor(passanger.userId, passanger.displayName))
        linearLayout.findViewById<TextView>(R.id.driverInitial).text = passanger.displayName.first().toUpperCase().toString()

        return linearLayout
    }

    private fun toogleFabIfNecessary(transport: TransportItem) {
        val loggedUser = SessionManager.getCurrentUser()!!
        if (transport.driver.sameUser(loggedUser))
            transport_detail_fab.withDrawable(R.drawable.ic_edit_white_24dp).setOnClickListener {
                transportViewModel.setTransport(transport)
                navigatorListener.replaceFragment(TransportBuilderFragment())
            }
        else if (!transport.isAlreadyInTransport(loggedUser) && (transport.isFull() || isInAnotherTransport(transport)))
            transport_detail_fab.hide()
        else {
            transport_detail_fab.show()
            transport_detail_fab.isEnabled = true
            if (!transport.isAlreadyInTransport(loggedUser))
                transport_detail_fab.withDrawable(R.drawable.ic_person_add_white_24dp).setOnClickListener {
                    it.isEnabled = false
                    transport.passangers.add(loggedUser)
                    eventViewModel.edit(transport) { _, errorMessage -> if (errorMessage!=null) showToast(errorMessage)}
                }
            else
                transport_detail_fab.withDrawable(R.drawable.ic_remove_white_24dp).setOnClickListener {
                    it.isEnabled = false
                    transport.passangers.remove(loggedUser)
                    eventViewModel.edit(transport) { _, errorMessage -> if (errorMessage!=null) showToast(errorMessage)}
                }
        }
    }

    private fun isInAnotherTransport(transport: TransportItem) =
            (eventViewModel.rides.value ?: ArrayList()).any { it.isAlreadyInTransport(SessionManager.getCurrentUser()!!) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        eventViewModel.rides.observe(this, Observer { rides ->
            transportViewModel.selectedDriver.value?.let { driver ->
                transportViewModel.setTransport(rides?.find { driver.sameUser(it.driver) }!!)
                llTransportDetail.removeAllViews()
                transportViewModel.transport.value?.passangers?.forEach { addPassanger(view!!, it) }
                toogleFabIfNecessary(transportViewModel.transport.value!!)
            }
        })

        transportViewModel.selectedDriver.observe(this, Observer { driver ->
            driver?.let {
                txtDriverName.text = it.displayName
                drawDriverCircle(it)
                transportViewModel.transport.value?.let { toogleFabIfNecessary(it) }
            }
        })
    }
}