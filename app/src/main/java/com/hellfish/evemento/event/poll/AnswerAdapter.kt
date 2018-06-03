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
    override fun layout(item : Int) : Int {
        return R.layout.poll_open_answer
    }

    override fun doOnItemOnBindViewHolder(view: TextView, item: Answer.Open, context: Context) {
        view.text = item.text
        view.setOnClickListener { callback(item) ; notifyDataSetChanged() }
    }
}

class ClosedAnswersAdapter(answers: List<Answer.Closed>, private val totalAmount: Int) : RecyclerAdapter<RelativeLayout, Answer.Closed>(answers) {
    override fun layout(item : Int) : Int {
        return R.layout.poll_closed_answer
    }

    override fun doOnItemOnBindViewHolder(view: RelativeLayout, item: Answer.Closed, context: Context) {
        val textView = view.closedAnswerTextView
        textView.text = "${item.text} - ${item.votes.toFloat() / totalAmount.toFloat() * 100}%"
        view.answerBackground.apply {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewTreeObserver.removeOnGlobalLayoutListener(this);
                    val finalWidth = (textView.width.toFloat() * item.percentageFrom(totalAmount)).roundToInt()
                    if(finalWidth != layoutParams.width) { animateWidth(finalWidth) }
                }
            })
        }
    }
}
