package com.hellfish.evemento.event.poll

import android.content.Context
import android.support.v7.widget.*
import com.hellfish.evemento.R
import com.hellfish.evemento.RecyclerAdapter
import com.hellfish.evemento.SessionManager
import kotlinx.android.synthetic.main.poll_content.view.*

class PollAdapter(public var polls: MutableList<Poll>, private val f: (Poll) -> Unit) : RecyclerAdapter<CardView, Poll>(polls) {
    companion object {
        const val openPollView = 0
        const val closedPollView = 1
    }

    override fun layout(item : Int): Int {
        return R.layout.poll_content
    }

    override fun getItemView(item: Poll): Int {
        return when (item) {
            is Poll.Votable -> openPollView
            is Poll.NoVotable -> closedPollView
        }
    }

    override fun doOnItemOnBindViewHolder(view: CardView, item: Poll, context: Context) {
        view.question.text = item.question

        view.answers.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = answersAdapter(item)
        }
    }

    fun updatePolls(newPolls: MutableList<Poll>) {
        if(polls.size == newPolls.size) {
            polls.zip(newPolls).forEachIndexed { i, (oldPoll, newPoll) ->
                if (oldPoll.totalVotes() != newPoll.totalVotes()) { polls[i] = newPoll; notifyItemChanged(i) }
            }
        } else {
            polls.clear()
            polls.addAll(newPolls)
            notifyDataSetChanged()
        }
    }

    private fun answersAdapter(poll : Poll) : RecyclerAdapter<*, *> {
        return when(poll) {
            is Poll.Votable -> OpenAnswersAdapter({ answer -> f(poll.choose(answer, SessionManager.getCurrentUser()!!.userId)) }, poll.answers)
            is Poll.NoVotable -> ClosedAnswersAdapter(poll.answers, poll.totalVotes())
        }
    }
}
