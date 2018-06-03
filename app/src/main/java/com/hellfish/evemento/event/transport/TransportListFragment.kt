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
        transports.add(TransportItem("Gus", "1"))
        transports.add(TransportItem("Gas", "0"))

        carsRecyclerView.apply {
            adapter = TransportAdapter(transports)
        }


    }
}