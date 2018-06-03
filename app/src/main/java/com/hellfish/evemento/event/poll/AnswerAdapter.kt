package com.hellfish.evemento.event.poll

import android.content.Context
import android.widget.TextView
import com.hellfish.evemento.R
import com.hellfish.evemento.RecyclerAdapter


class OpenAnswersAdapter(private val callback: (Answer.Open) -> Unit, answers: List<Answer.Open>) : RecyclerAdapter<TextView, Answer.Open>(answers) {
    override fun layout(item : Int) : Int {
        return R.layout.poll_open_answer
    }

    override fun doOnItemOnBindViewHolder(view: TextView, item: Answer.Open, context: Context) {
        view.text = item.text
        view.setOnClickListener { callback(item) }
    }
}

class ClosedAnswersAdapter(answers: List<Answer.Closed>, private val totalAmount: Int) : RecyclerAdapter<TextView, Answer.Closed>(answers) {
    override fun layout(item : Int) : Int {
        return R.layout.poll_closed_answer
    }

    override fun doOnItemOnBindViewHolder(view: TextView, item: Answer.Closed, context: Context) {
        view.text = "${item.text} - ${item.votes.toFloat() / totalAmount.toFloat() * 100}%"
    }
}