package com.hellfish.evemento.event.transport

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.hellfish.evemento.EventViewModel
import com.hellfish.evemento.NavigatorFragment
import com.hellfish.evemento.R
import com.hellfish.evemento.SessionManager
import com.hellfish.evemento.api.User
import com.hellfish.evemento.event.guest.UserColor
import com.hellfish.evemento.extensions.withDrawable
import kotlinx.android.synthetic.main.fragment_transport_detail.*
import kotlinx.android.synthetic.main.fragment_transport_detail.view.*

class TransportDetailFragment() : NavigatorFragment(), UserColor {

    lateinit var driver: User
    private lateinit var loggedInUser: User
    private lateinit var transport: TransportItem
    private lateinit var eventViewModel: EventViewModel

    private lateinit var transports: MutableList<TransportItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventViewModel = ViewModelProviders.of(activity!!).get(EventViewModel::class.java)
        eventViewModel.rides.observe(this, Observer { rides ->
            transports = rides ?: ArrayList()
            transport = rides?.find { driver.sameUser(it.driver) }!!
            llTransportDetail.removeAllViews()
            transport.passangers.forEach { addPassanger(view!!, it) }
            toogleFabIfNecessary(transport)

        })
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_transport_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val argDriver = arguments?.getParcelable<User>("driver")
        this.loggedInUser = SessionManager.getCurrentUser()!!

        if (argDriver != null) {
            driver = argDriver
            view.txtDriverName.text = driver.displayName
            drawDriverCircle(view, driver, context!!)
            if (::transport.isInitialized) toogleFabIfNecessary(transport)
        }
    }

    private fun drawDriverCircle(view: View, item: User, context: Context) {
        DrawableCompat.setTint(view.driverCircle.drawable, userColor(item.userId, item.displayName))
        view.driverInitial.text = item.displayName.first().toUpperCase().toString()
    }


    private fun addPassanger(view: View, passanger: User) {
        val passagerView = createPassangerView(view.context, passanger)
        view.llTransportDetail.addView(passagerView)
    }

    private fun createPassangerView(context: Context, passanger: User): View {
        val linearLayout = layoutInflater.inflate(R.layout.transport_detail_passanger, null)

        linearLayout.findViewById<TextView>(R.id.txtPassangerName).setText(passanger.displayName)
        DrawableCompat.setTint(linearLayout.findViewById<ImageView>(R.id.driverCircle).drawable,
                userColor(passanger.userId, passanger.displayName))
        linearLayout.findViewById<TextView>(R.id.driverInitial).text = passanger.displayName.first().toUpperCase().toString()

        return linearLayout
    }

    private fun createPassangerCircle(context: Context, passanger: User): View {
        return View(context)
    }

    private fun createPassangerTV(context: Context, passanger: User) : TextView {
        return TextView(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setTextAppearance(R.style.Base_TextAppearance_AppCompat_Medium)
            } else {
                setTextAppearance(context, R.style.Base_TextAppearance_AppCompat_Medium)
            }
            text = passanger.displayName
        }
    }

    private fun toogleFabIfNecessary(transport: TransportItem) {
        if (transport.driver.sameUser(this.loggedInUser))
            transport_detail_fab.withDrawable(R.drawable.ic_edit_white_24dp).setOnClickListener {
                val transportBuilderFragment = TransportBuilderFragment()
                val args = Bundle()
                args.putParcelable("transport", transport)
                transportBuilderFragment.arguments = args
                navigatorListener.replaceFragment(transportBuilderFragment)
            }
        else if (transport.isFull() || (transports.any { it.isAlreadyInTransport(loggedInUser) } && !transport.isAlreadyInTransport(loggedInUser)))
            transport_detail_fab.hide()
        else {
            transport_detail_fab.show()
            if (!transport.isAlreadyInTransport(loggedInUser))
                transport_detail_fab.withDrawable(R.drawable.ic_person_add_white_24dp).setOnClickListener {
                    transport.passangers.add(loggedInUser)
                    eventViewModel.edit(transport) { _, errorMessage -> showToast(errorMessage ?: R.string.api_error_fetching_data)}
                }
            else
                transport_detail_fab.withDrawable(R.drawable.ic_remove_white_24dp).setOnClickListener {
                    transport.passangers.remove(loggedInUser)
                    eventViewModel.edit(transport) { _, errorMessage -> showToast(errorMessage ?: R.string.api_error_fetching_data)}
                }
        }
    }
}