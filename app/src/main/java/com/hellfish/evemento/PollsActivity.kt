package com.hellfish.evemento

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_polls.*
import kotlinx.android.synthetic.main.closed_answer_polls.view.*
import kotlinx.android.synthetic.main.content_polls.view.*
import kotlinx.android.synthetic.main.open_answer_polls.view.*

class PollsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_polls)

        val polls = mutableListOf(Poll.Open("¿Comes carne?", listOf(Answer.Open("Sí", 2), Answer.Open("No", 1))),
                                    Poll.Closed("¿Llevas mascota?",
                                                listOf(Answer.Closed("Sí", 3), Answer.Closed("No", 5))))

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = PollAdapter(polls)
        }
    }

    sealed class Poll(open val question: String, open val answers: List<Answer>) {

        fun totalVotes() : Int = answers.sumBy { it.votes }

        class Open(override val question: String,
                        override val answers: List<Answer.Open>) : Poll(question, answers) {
            fun choose(answer: Answer.Open) : Poll.Closed {
                val updatedAnswers = answers.map { anAnswer -> if (answer == anAnswer) { anAnswer.vote() } else { anAnswer.close() } }
                return Poll.Closed(question, updatedAnswers)
            }
        }

        class Closed(override val question: String,
                          override val answers: List<Answer.Closed>) : Poll(question, answers)
    }

    sealed class Answer(val text: String, val votes: Int) {
        class Open(text: String, votes: Int) : Answer(text, votes) {
            fun vote() : Answer.Closed = Answer.Closed(text, votes + 1)

            fun close() : Answer.Closed = Answer.Closed(text, votes)
        }
        class Closed(text: String, votes: Int) : Answer(text, votes)
    }

    class PollAdapter(private val polls: MutableList<Poll>) : RecyclerAdapter<CardView, Poll>(polls) {
        companion object {
            val openPollView = 0
            val closedPollView = 1
        }

        override fun layout(viewType : Int): Int {
            return R.layout.content_polls
        }

        override fun getItemView(item: Poll): Int {
            return when (item) {
                is Poll.Open -> openPollView
                is Poll.Closed -> closedPollView
            }
        }

        override fun doOnItemOnBindViewHolder(view: CardView, item: Poll, context: Context) {
            view.question.text = item.question

            view.answers.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = answersAdapter(item)
            }
        }

        private fun answersAdapter(poll : Poll) : RecyclerAdapter<TextView, *> {
            return when(poll) {
                is Poll.Open -> OpenAnswersAdapter({ answer -> polls[polls.indexOf(poll)] = poll.choose(answer); notifyDataSetChanged() }, poll.answers)
                is Poll.Closed -> ClosedAnswersAdapter(poll.answers, poll.totalVotes())
            }
        }
    }

    class OpenAnswersAdapter(private val callback: (Answer.Open) -> Unit, answers: List<Answer.Open>) : RecyclerAdapter<TextView, Answer.Open>(answers) {
        override fun layout(viewType : Int) : Int {
            return R.layout.open_answer_polls
        }

        override fun doOnItemOnBindViewHolder(view: TextView, item: Answer.Open, context: Context) {
            view.text = item.text
            view.setOnClickListener {
                callback(item)
                Toast.makeText(context, "Elegiste ${view.text}!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    class ClosedAnswersAdapter(answers: List<Answer.Closed>, val totalAmount: Int) : RecyclerAdapter<TextView, Answer.Closed>(answers) {
        override fun layout(viewType : Int) : Int {
            return R.layout.closed_answer_polls
        }

        override fun doOnItemOnBindViewHolder(view: TextView, item: Answer.Closed, context: Context) {
            view.text = item.text + " - ${item.votes.toFloat() / totalAmount.toFloat() * 100}%"
            view.setOnClickListener {
                Toast.makeText(context, "Nopes", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
