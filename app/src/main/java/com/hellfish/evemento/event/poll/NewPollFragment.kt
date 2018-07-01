package com.hellfish.evemento.event.poll

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.hellfish.evemento.EventViewModel
import com.hellfish.evemento.NavigatorFragment
import com.hellfish.evemento.NetworkManager
import com.hellfish.evemento.R
import com.hellfish.evemento.R.string.createPollSuccessfully
import com.hellfish.evemento.R.string.title_fragment_new_poll
import com.hellfish.evemento.extensions.getChildren

import kotlinx.android.synthetic.main.fragment_new_poll.*

class NewPollFragment : NavigatorFragment() {

    override val titleId = title_fragment_new_poll

    lateinit var eventViewModel: EventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventViewModel = ViewModelProviders.of(activity!!).get(EventViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_new_poll, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        newPollFab.setOnClickListener { _ ->
            val newPoll = Poll.Votable(question=editPollQuestion.text.toString(),
                                       answers=answers(),
                                       eventId=eventViewModel.selectedEvent.value!!.eventId,
                                       pollId="")
            NetworkManager.pushPoll(newPoll, { pollId,_ ->
                pollId?.let { eventViewModel.add(newPoll.setId(it)) }
                showToast(createPollSuccessfully)
                activity!!.onBackPressed()
            })
        }
        addAnswer()
        addAnswer()
        newPollAddAnswerButton.setOnClickListener { _ -> addAnswer() }
    }

    private fun answers() = newPollAnswersLinearLayout.getChildren<EditText>().map { editText ->
        Answer.Open(editText.text.toString(), listOf())
    }

    private fun addAnswer() {
        val newEditTextView = EditText(context)
        newEditTextView.setSingleLine(true)
        newEditTextView.hint = "An answer"
        newPollAnswersLinearLayout.addView(newEditTextView)
    }
}
