package com.hellfish.evemento

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.widget.TextView
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_polls.*
import kotlinx.android.synthetic.main.content_polls.view.*

class PollsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_polls)

        val polls = listOf(Poll("¿Comes carne?", listOf("Sí", "No")))

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = PollAdapter(polls)
        }
    }

    data class Poll(val question: String, val answers: List<String>)

    class PollAdapter(polls: List<Poll>) : RecyclerAdapter<CardView, Poll>(polls) {
        override fun layout(): Int {
            return R.layout.content_polls
        }

        override fun doOnItemOnBindViewHolder(view: CardView, item: Poll, context: Context) {
            view.question.text = item.question
            view.answers.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = AnswersAdapter(item.answers)
            }
        }
    }

    class AnswersAdapter(answers: List<String>) : RecyclerAdapter<TextView, String>(answers) {
        override fun layout() : Int {
            return R.layout.answer_polls
        }

        override fun doOnItemOnBindViewHolder(view: TextView, item: String, context: Context) {
            view.text = item
            view.setOnClickListener {
                Toast.makeText(context, "Elegiste ${view.text}!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
