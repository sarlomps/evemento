package com.hellfish.evemento.event.poll

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.hellfish.evemento.R

import kotlinx.android.synthetic.main.activity_poll.*

class PollActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poll)

        val polls = mutableListOf(Poll.Open("¿Comes carne?", listOf(Answer.Open("Sí", 2), Answer.Open("No", 1))),
                Poll.Closed("¿Llevas mascota?",
                        listOf(Answer.Closed("Sí", 3), Answer.Closed("No", 5))))

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = PollAdapter(polls)
        }
    }

}
