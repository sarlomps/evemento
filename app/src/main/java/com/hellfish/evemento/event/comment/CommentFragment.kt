package com.hellfish.evemento.event.comment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellfish.evemento.EventViewModel
import com.hellfish.evemento.NavigatorFragment
import com.hellfish.evemento.R
import com.hellfish.evemento.R.string.title_fragment_comments_list
import com.hellfish.evemento.R.string.title_fragment_poll_list
import com.hellfish.evemento.api.Comment
import kotlinx.android.synthetic.main.fragment_comment_list.*

import kotlinx.android.synthetic.main.fragment_poll_list.*

class CommentFragment : NavigatorFragment() {

    override val titleId = title_fragment_comments_list

    lateinit var eventViewModel: EventViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventViewModel = ViewModelProviders.of(activity!!).get(EventViewModel::class.java)
        eventViewModel.loadComments { _, _ -> Unit }
        eventViewModel.comments.observe(this, Observer { comments ->
            commentsRecyclerView.apply {
                comments?.let { adapter = CommentAdapter(it) }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_comment_list, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val comments = mutableListOf<Comment>()

        commentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = CommentAdapter(comments)
        }
    }

}
