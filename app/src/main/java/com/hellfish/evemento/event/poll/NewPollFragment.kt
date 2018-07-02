package com.hellfish.evemento.event.poll

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.TextViewCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.hellfish.evemento.EventViewModel
import com.hellfish.evemento.NavigatorFragment
import com.hellfish.evemento.NetworkManager
import com.hellfish.evemento.R
import com.hellfish.evemento.R.string.*
import com.hellfish.evemento.extensions.getChildren
import com.hellfish.evemento.extensions.hideKeyboard

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
            if(!validatePollFields()) { return@setOnClickListener }
            val newPoll = Poll.Votable(question=editPollQuestion.text.toString(),
                                       answers=answers(),
                                       eventId=eventViewModel.selectedEvent.value!!.eventId,
                                       pollId="")
            NetworkManager.pushPoll(newPoll, { pollId,_ ->
                pollId?.let { eventViewModel.add(newPoll.setId(it)) }
                showToast(createPollSuccessfully)
                activity?.run {
                    onBackPressed()
                    hideKeyboard()
                }
            })
        }
        addAnswer(withDeleteButton=false)
        addAnswer(withDeleteButton=false)
        val maxAmountOfAnswers = 7
        newPollAddAnswerButton.setOnClickListener { _ ->
            addAnswer(withDeleteButton=true)
            if(newPollAnswersLinearLayout.getChildren<View>().size >= maxAmountOfAnswers) {
                newPollAddAnswerButton.visibility = View.GONE
            }
        }
    }

    private fun answers() =
            newPollAnswersLinearLayout.getChildren<LinearLayout>().map { it.getChildren<View>().find { it is EditText } as EditText }
                    .map { editText -> Answer.Open(editText.text.toString(), listOf()) }

    private fun addAnswer(withDeleteButton : Boolean) {
        val newEditAnswer = LinearLayout(context)
        val newEditTextView = EditText(context).apply {
            TextViewCompat.setTextAppearance(this, android.R.style.TextAppearance_Material_Subhead)
            layoutParams = TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.85f).apply {
                textSize = 22f
                setPadding(5, 0, 0, 20)
                hint = "An answer"
                setSingleLine(true)
                height=120
                setTextColor(ContextCompat.getColor(context, android.R.color.black))
            }
        }

        val deleteAnswerButton = Button(context).apply {
            layoutParams = TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.1f)
            background = ContextCompat.getDrawable(context!!, R.drawable.ic_close_grey_24dp)
            setOnClickListener {
                newPollAnswersLinearLayout.removeView(newEditAnswer)
                newPollAddAnswerButton.visibility = View.VISIBLE
            }
        }
        newEditAnswer.apply {
            addView(newEditTextView)
            if(withDeleteButton) { addView(deleteAnswerButton) }
        }

        newPollAnswersLinearLayout.addView(newEditAnswer)
    }

    private fun validatePollFields() : Boolean {
        val answers : List<EditText> = newPollAnswersLinearLayout.getChildren<LinearLayout>().map { it.getChildren<View>().find { it is EditText }!! as EditText }
        val pollFields = answers.plus(editPollQuestion)
        answers.forEach { answer ->
            answer.validateText(errorDuplicatePollAnswer, { answerText ->
                answers.takeWhile { it != answer }.all { it.text.toString() != answerText }
            })
        }
        pollFields.forEach { it.validateText(errorEmptyPollField, { it.isNotEmpty() }) }
        return pollFields.all { it.error?.isEmpty() ?: true }
    }
}


fun EditText.validateText(errorMessage : Int, validation : (String) -> (Boolean)) {
    if(!validation(this.text.toString())) { this.error = resources.getString(errorMessage) }
}