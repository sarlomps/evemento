package com.hellfish.evemento.event.comment

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.hellfish.evemento.EventViewModel
import com.hellfish.evemento.NavigatorFragment
import com.hellfish.evemento.R
import com.hellfish.evemento.R.string.title_fragment_comments_list
import com.hellfish.evemento.SessionManager
import com.hellfish.evemento.api.Comment
import kotlinx.android.synthetic.main.fragment_comment_list.*

class CommentFragment : NavigatorFragment() {

    override val titleId = title_fragment_comments_list

    lateinit var eventViewModel: EventViewModel
    lateinit var dialogInput: EditText
    lateinit var clickDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventViewModel = ViewModelProviders.of(activity!!).get(EventViewModel::class.java)
        eventViewModel.loadComments { _ -> showToast(R.string.errorLoadingComments) }
        eventViewModel.comments.observe(this, Observer { comments ->
            comments?.let { commentsRecyclerView.adapter = CommentAdapter(it.toMutableList(), editListener()) }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialogInput = EditText(activity)
        clickDialog = createAlertDialog(R.string.commentDialogTitle,
                dialogInput,
                negativeButtonDefinition = Pair(R.string.cancel, { _, _ -> Unit }),
                neutralButtonDefinition = Pair(R.string.delete, { _, _ -> Unit }))
        return inflater.inflate(R.layout.fragment_comment_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        commentListFab.setOnClickListener(addListener())
        commentsRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun addListener() = View.OnClickListener {
        dialogInput.setText("")
        clickDialog.run {
            setButton(Dialog.BUTTON_POSITIVE, getString(R.string.accept)) { _, _ ->
                eventViewModel.add(Comment(
                        "",
                        SessionManager.getCurrentUser()!!.userId,
                        SessionManager.getCurrentUser()!!.displayName,
                        dialogInput.text.toString()
                ))
            }
            show()
            getButton(Dialog.BUTTON_NEUTRAL)?.visibility = View.GONE
        }
    }

    private fun editListener() = { comment: Comment ->
        View.OnClickListener {
            dialogInput.setText(comment.message)
            clickDialog.run {
                setButton(Dialog.BUTTON_POSITIVE, getString(R.string.accept)) { _, _ -> eventViewModel.edit(comment.copy(message = dialogInput.text.toString())) }
                setButton(Dialog.BUTTON_NEUTRAL, getString(R.string.delete)) { _, _ -> eventViewModel.remove(comment) }
                show()
                getButton(Dialog.BUTTON_NEUTRAL).visibility = View.VISIBLE
            }
        }
    }

}

