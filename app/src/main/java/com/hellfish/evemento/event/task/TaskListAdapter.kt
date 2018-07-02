package com.hellfish.evemento.event.task

import android.content.Context
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.CardView
import android.view.View
import android.widget.TextView
import com.hellfish.evemento.R
import com.hellfish.evemento.RecyclerAdapter
import com.hellfish.evemento.event.guest.CircleColor
import kotlinx.android.synthetic.main.comment_content.view.*
import kotlinx.android.synthetic.main.fragment_task_list_item.view.*

class TaskListAdapter(private val taskItems: MutableList<TaskItem>, val noOneInCharge: String, val editTaskItem: (TaskItem) -> View.OnClickListener) : RecyclerAdapter<CardView, TaskItem>(taskItems), CircleColor {
    override fun doOnEmptyOnBindViewHolder(): (view: TextView, context: Context?) -> Unit {
        return {view, _ ->
            view.text = "No tasks yet!"
        }
    }

    override fun layout(item: Int): Int {
        return when (item) {
            EMPTY_VIEW -> R.layout.fragment_event_list_empty
            else -> R.layout.fragment_task_list_item
        }
    }


    override fun doOnItemOnBindViewHolder(): (view: CardView, item: TaskItem?, context: Context?) -> Unit {
        return { view, item, context ->
            item?.let {
                DrawableCompat.setTint(view.taskCircle.drawable, circleColor(item.description, item.description.reversed()))
                view.taskInitial.text = item.description.first().toUpperCase().toString()

                view.taskItemDescription?.text = item.description
                view.taskItemResponsible?.text = item.responsible

                view.taskItemResponsibleHeader.visibility = if (item.responsible == noOneInCharge) View.GONE
                                                            else View.VISIBLE

                view.setOnClickListener(editTaskItem(item))
            }
        }
    }
}