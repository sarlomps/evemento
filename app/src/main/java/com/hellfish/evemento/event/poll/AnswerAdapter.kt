package com.hellfish.evemento.event.poll

import android.content.Context
import android.widget.RelativeLayout
import android.widget.TextView
import com.hellfish.evemento.R
import com.hellfish.evemento.RecyclerAdapter
import kotlinx.android.synthetic.main.poll_closed_answer.view.*
import android.view.ViewTreeObserver
import com.hellfish.evemento.extensions.animateWidth
import kotlin.math.roundToInt

class OpenAnswersAdapter(private val callback: (Answer.Open) -> Unit, answers: List<Answer.Open>) : RecyclerAdapter<TextView, Answer.Open>(answers) {
    override fun doOnEmptyOnBindViewHolder(): (view: TextView, context: Context?) -> Unit {
        return {_, _ -> }
    }

    override fun doOnItemOnBindViewHolder(): (view: TextView, item: Answer.Open, context: Context?) -> Unit {
        return { view, item, _ ->
            view.text = item.text
            view.setOnClickListener { callback(item) ; notifyDataSetChanged() }

        }
    }

    override fun layout(item: Int): Int {
        return when (item) {
            EMPTY_VIEW -> R.layout.fragment_event_list_empty
            else -> R.layout.poll_open_answer
        }
    }

}

class ClosedAnswersAdapter(answers: List<Answer.Closed>, private val totalAmount: Int) : RecyclerAdapter<RelativeLayout, Answer.Closed>(answers) {
    override fun doOnEmptyOnBindViewHolder(): (view: TextView, context: Context?) -> Unit {
        // Not necessary...
        return {_, _ -> }
    }

    override fun doOnItemOnBindViewHolder(): (view: RelativeLayout, item: Answer.Closed, context: Context?) -> Unit {
        return { view, item, _ ->
            val textView = view.closedAnswerTextView

            textView.text = textView.resources.getString(R.string.pollAnswerWithVotes, item.text, item.votesAmount, totalAmount)
            view.answerBackground.apply {
                val finalWidth = (textView.layoutParams.width.toFloat() * item.percentageFrom(totalAmount)).roundToInt()
                animateWidth(finalWidth)
            }
            view.answerBackground.apply {
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                        val finalWidth = (textView.width.toFloat() * item.percentageFrom(totalAmount)).roundToInt()
                        animateWidth(finalWidth)
                    }
                })
            }

        }
    }

    override fun layout(item : Int) : Int {
        return R.layout.poll_closed_answer
    }
}
