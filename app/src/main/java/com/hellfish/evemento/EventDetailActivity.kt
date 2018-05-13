package com.hellfish.evemento

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.event_detail_tool_bar.*

class EventDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)
        
        setSupportActionBar(eventDetailToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        eventTitle.text = "Titulo"
    }

}
