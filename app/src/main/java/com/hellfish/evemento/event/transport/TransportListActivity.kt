package com.hellfish.evemento.event.transport

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.LinearLayout
import com.hellfish.evemento.R

class TransportListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transport_list)

        var recycler: RecyclerView = findViewById(R.id.carsRecyclerView)
        recycler.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        val transports = ArrayList<TransportItem>()
        transports.add(TransportItem("Gus", "1"))
        transports.add(TransportItem("Gas", "0"))

        var adapter = TransportAdapter(transports)
        recycler.adapter = adapter
    }
}