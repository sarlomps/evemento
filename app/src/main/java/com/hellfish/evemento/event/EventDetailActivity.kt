package com.hellfish.evemento.event

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hellfish.evemento.R
import kotlinx.android.synthetic.main.event_detail_tool_bar.*

class EventDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)
        
        setSupportActionBar(eventDetailToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        eventTitle.text = getString(R.string.default_event_title)
    }

}
