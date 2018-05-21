package com.hellfish.evemento

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_polls.*
import kotlinx.android.synthetic.main.answer_polls.view.*
import kotlinx.android.synthetic.main.content_polls.view.*

class PollsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_polls)

        val viewManager = LinearLayoutManager(this)
        val polls = listOf(Poll("¿Comes carne?", listOf("Sí", "No")))
        val viewAdapter = PollAdapter(polls)
        recyclerView.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    data class Poll(val question: String, val answers: List<String>)

    class PollAdapter(val polls: List<Poll>) : RecyclerView.Adapter<PollAdapter.ViewHolder>() {
        class ViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView)

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val poll = polls[position]
            holder.cardView.question.text = poll.question
            holder.cardView.answers.adapter = AnswersAdapter(poll.answers)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val cardView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.content_polls, parent, false) as CardView

            cardView.answers.apply {
                layoutManager = LinearLayoutManager(parent.context)
            }

            return ViewHolder(cardView)
        }

        override fun getItemCount(): Int {
            return polls.size
        }
    }

    class AnswersAdapter(val answers: List<String>) : RecyclerView.Adapter<AnswersAdapter.ViewHolder>() {
        class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val textView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.answer_polls, parent, false) as TextView

            textView.setOnClickListener { v ->
                Toast.makeText(parent.context, "Elegiste ${v.textView.text}!", Toast.LENGTH_SHORT).show()
            }

            return ViewHolder(textView)
        }

        override fun getItemCount(): Int {
            return answers.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textView.text = answers[position]
        }
    }
}
