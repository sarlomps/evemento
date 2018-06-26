package com.hellfish.evemento.event.transport

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hellfish.evemento.EventViewModel
import com.hellfish.evemento.NavigatorFragment
import com.hellfish.evemento.R
import com.hellfish.evemento.event.task.TaskListFragment
import kotlinx.android.synthetic.main.fragment_transport_detail.*
import kotlinx.android.synthetic.main.fragment_transport_list.*


class TransportListFragment : NavigatorFragment() {

    lateinit var eventViewModel: EventViewModel
    private lateinit var loggedInUser: UserMiniDetail

    var transports: List<TransportItem> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventViewModel = ViewModelProviders.of(activity!!).get(EventViewModel::class.java)
        eventViewModel.rides.observe(this, Observer { rides ->
            val transports = rides ?: ArrayList()
            this.transports = transports;
            toogleFabIfNecessary()
            carsRecyclerView.apply {
                adapter = TransportAdapter(transports, navigatorListener)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_transport_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.loggedInUser = UserMiniDetail("juanchiLoggeado", "sarlanga")


        carsRecyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayout.VERTICAL, false)

        transport_list_fab.setOnClickListener{ navigatorListener.replaceFragment(TransportBuilderFragment()) }

        if(transports.isNotEmpty()) {
            carsRecyclerView.adapter = TransportAdapter(transports, navigatorListener)
        } else {
            //TODO no rides
        }
    }
    private fun toogleFabIfNecessary() {
        if (transports.any { it.isAlreadyInTransport(loggedInUser) }) {
            transport_list_fab.hide()
        } else {
            transport_list_fab.show()
        }
    }

}