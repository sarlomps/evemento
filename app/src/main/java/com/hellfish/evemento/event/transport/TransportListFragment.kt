package com.hellfish.evemento.event.transport

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hellfish.evemento.NavigatorFragment
import com.hellfish.evemento.R
import kotlinx.android.synthetic.main.fragment_transport_list.*


class TransportListFragment : NavigatorFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_transport_list, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        carsRecyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayout.VERTICAL, false)
        val transports = ArrayList<TransportItem>()
        val driver1 = UserMiniDetail("Gus", "Sarlanga")
        val driver2 = UserMiniDetail("Gas", "Sarlanga")
        val pass_1_1 = UserMiniDetail("juan", "Sarlanga")
        val pass_1_2 = UserMiniDetail("juan", "Sarlanga")
        val pass_2_1 = UserMiniDetail("Nico", "Sarlanga")
        val pass_2_2 = UserMiniDetail("Nico", "Sarlanga")
        transports.add(TransportItem(driver1, listOf(pass_1_1, pass_1_2), 4))
        transports.add(TransportItem(driver2, listOf(pass_2_1, pass_2_2), 3))

        carsRecyclerView.apply {
            adapter = TransportAdapter(transports, navigatorListener)
        }


    }
}