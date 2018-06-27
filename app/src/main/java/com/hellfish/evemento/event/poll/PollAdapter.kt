package com.hellfish.evemento.event.poll

import android.content.Context
import android.support.v7.widget.*
import com.hellfish.evemento.R
import com.hellfish.evemento.RecyclerAdapter
import kotlinx.android.synthetic.main.poll_content.view.*

class PollAdapter(polls: MutableList<PollObject>, private val f: (PollObject) -> Unit) : RecyclerAdapter<CardView, PollObject>(polls) {
    companion object {
        const val openPollView = 0
        const val closedPollView = 1
    }

    override fun layout(item : Int): Int {
        return R.layout.poll_content
    }

    override fun getItemView(item: PollObject): Int {
        return when (item) {
            is PollObject.Votable -> openPollView
            is PollObject.NoVotable -> closedPollView
        }
    }

    override fun doOnItemOnBindViewHolder(view: CardView, item: PollObject, context: Context) {
        view.question.text = item.question

        view.answers.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = answersAdapter(item)
        }
    }

    private fun answersAdapter(poll : PollObject) : RecyclerAdapter<*, *> {
        return when(poll) {
            is PollObject.Votable -> OpenAnswersAdapter({ answer -> f(poll.choose(answer)) }, poll.answers)
            is PollObject.NoVotable -> ClosedAnswersAdapter(poll.answers, poll.totalVotes())
        }
    }
}
