package com.sarlomps.evemento.event.poll

import android.content.Context
import android.widget.TextView
import com.sarlomps.evemento.R
import com.sarlomps.evemento.RecyclerAdapter
import kotlinx.android.synthetic.main.poll_closed_answer.view.*
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import com.sarlomps.evemento.SessionManager
import com.sarlomps.evemento.extensions.animateWidth
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

class ClosedAnswersAdapter(answers: List<Answer.Closed>, private val totalAmount: Int) : RecyclerAdapter<FrameLayout, Answer.Closed>(answers) {
    override fun doOnEmptyOnBindViewHolder(): (view: TextView, context: Context?) -> Unit {
        // Not necessary...
        return {_, _ -> }
    }

    override fun doOnItemOnBindViewHolder(): (view: FrameLayout, item: Answer.Closed, context: Context?) -> Unit {
        return { view, item, _ ->
            view.run {
                closedAnswerTextView.text =
                        if(!item.votes.contains(SessionManager.getCurrentUser()!!.userId)) item.text
                        else String.format("%s %s", item.text, String(Character.toChars(0x2714)))

                closedAnswerPorcentage.text = String.format("%d%s", (item.percentageFrom(totalAmount) * 100).roundToInt(), "%")

                answerBackground.let {
                    val finalWidth = (closedAnswerTextView.layoutParams.width.toFloat() * item.percentageFrom(totalAmount)).roundToInt()
                    it.animateWidth(finalWidth)
                }
                answerBackground.apply {
                    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            viewTreeObserver.removeOnGlobalLayoutListener(this)
                            val finalWidth = (view.width.toFloat() * item.percentageFrom(totalAmount)).roundToInt()
                            animateWidth(finalWidth)
                        }
                    })
                }
            }
        }
    }

    override fun layout(item : Int) : Int {
        return R.layout.poll_closed_answer
    }
}
