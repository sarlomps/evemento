package com.sarlomps.evemento.event.poll

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat
import android.support.v4.widget.TextViewCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.sarlomps.evemento.EventViewModel
import com.sarlomps.evemento.NavigatorFragment
import com.sarlomps.evemento.NetworkManager
import com.sarlomps.evemento.R
import com.sarlomps.evemento.R.string.*
import com.sarlomps.evemento.extensions.getChildren
import com.sarlomps.evemento.extensions.hideKeyboard

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
        val maxAmountOfAnswers = 5
        newPollAddAnswerButton.setOnClickListener { _ ->
            addAnswer(withDeleteButton=true)
            if(newPollAnswersLinearLayout.getChildren<View>().size >= maxAmountOfAnswers) {
                newPollAddAnswerButton.visibility = View.GONE
            }
        }
        editPollQuestion.cleanErrorsOnTextChange(editPollQuestionLayout)
    }

    private fun answers() =
            newPollAnswersLinearLayout.getChildren<TextInputLayout>().map { toEditText(it) }
                    .map { editText -> Answer.Open(editText.text.toString(), listOf()) }

    private fun addAnswer(withDeleteButton : Boolean) {
        val textInputLayout = TextInputLayout(context)
        val newEditTextView = EditText(context).apply {
            TextViewCompat.setTextAppearance(this, android.R.style.TextAppearance_Material_Subhead)
            id = View.generateViewId()
            layoutParams = TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.85f).apply {
                textSize = 20f
                setPadding(5, 0, 0, 20)
                hint = "An answer"
                setSingleLine(true)
                height=120
                setTextColor(ContextCompat.getColor(context, android.R.color.black))
            }
            cleanErrorsOnTextChange(textInputLayout)
        }

        val deleteAnswerButton = Button(context).apply {
            layoutParams = TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.15f)
            background = ContextCompat.getDrawable(context!!, R.drawable.ic_close_grey_24dp)
            setOnClickListener {
                newPollAnswersLinearLayout.removeView(textInputLayout)
                newPollAddAnswerButton.visibility = View.VISIBLE
            }
        }
        textInputLayout.apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            addView(LinearLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                orientation = LinearLayout.HORIZONTAL
                addView(newEditTextView)
                if(withDeleteButton) { addView(deleteAnswerButton) }
            })
        }

        newPollAnswersLinearLayout.addView(textInputLayout)
    }

    private fun validatePollFields() : Boolean {
        val answerLayouts = newPollAnswersLinearLayout.getChildren<TextInputLayout>()
        val answers : List<EditText> = answerLayouts.map { toEditText(it) }
        answerLayouts.forEach { answerLayout ->
            toEditText(answerLayout).validateText(answerLayout, errorDuplicatePollAnswer, { answerText ->
                answers.takeWhile { Log.d("IT.ID", it.id.toString());Log.d("ANANSWER.ID", toEditText(answerLayout).id.toString());it.id != toEditText(answerLayout).id }.all { it.text.toString() != answerText }
            })
        }
        editPollQuestion.validateText(editPollQuestionLayout, errorEmptyPollField, { it.isNotEmpty() })
        answerLayouts.forEach { toEditText(it).validateText(it, errorEmptyPollField, { it.isNotEmpty() }) }
        return answerLayouts.plus(editPollQuestionLayout).all { it.error == null }
    }
}

fun toEditText(textInputLayout : TextInputLayout) = (textInputLayout.getChildAt(1) as LinearLayout).getChildren<EditText>().first()

fun EditText.validateText(textInputLayout : TextInputLayout, errorMessage : Int, validation : (String) -> (Boolean)) {
    if(!validation(this.text.toString())) { textInputLayout.error = resources.getString(errorMessage) }
}

fun EditText.cleanErrorsOnTextChange(textInputLayout : TextInputLayout) {
    addTextChangedListener(object :TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { textInputLayout.error = null }

    })
}