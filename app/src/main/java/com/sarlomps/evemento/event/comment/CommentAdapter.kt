package com.sarlomps.evemento.event.comment

import android.content.Context
import android.graphics.Paint
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.*
import android.view.View
import android.widget.TextView
import com.sarlomps.evemento.R
import com.sarlomps.evemento.RecyclerAdapter
import com.sarlomps.evemento.api.Comment
import com.sarlomps.evemento.event.guest.CircleColor
import kotlinx.android.synthetic.main.comment_content.view.*

class CommentAdapter(comments: MutableList<Comment>, val editListener: (Comment) -> View.OnClickListener) : RecyclerAdapter<CardView, Comment>(comments), CircleColor {
    override fun doOnEmptyOnBindViewHolder(): (view: TextView, context: Context?) -> Unit {
        return { view, _ ->
            view.text = "No comments yet"
        }
    }

    override fun doOnItemOnBindViewHolder(): (view: CardView, item: Comment, context: Context?) -> Unit {
        return { view, item, _ ->
            DrawableCompat.setTint(view.commentCircle.drawable, circleColor(item.userId, item.name))
            view.commentInitial.text = item.name.first().toUpperCase().toString()

            view.commentUserName.text = item.name
            view.commentUserName.paintFlags = view.commentUserName.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            view.commentMessage.text = item.message

            view.setOnClickListener(editListener(item))
        }
    }

    override fun layout(item : Int): Int {
        return when (item) {
            EMPTY_VIEW -> R.layout.fragment_event_list_empty
            else -> R.layout.comment_content
        }
    }
}
