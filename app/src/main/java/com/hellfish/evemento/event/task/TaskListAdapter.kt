package com.hellfish.evemento.event.task

import android.content.Context
import android.support.v7.widget.CardView
import android.view.View
import android.widget.TextView
import com.hellfish.evemento.R
import com.hellfish.evemento.RecyclerAdapter
import kotlinx.android.synthetic.main.fragment_task_list_item.view.*

class TaskListAdapter(private val taskItems: MutableList<TaskItem>, val editTaskItem: (TaskItem) -> View.OnClickListener) : RecyclerAdapter<CardView, TaskItem>(taskItems) {
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
        return { view, item, _ ->
            item?.let{

                view.task_item_description?.text = item.description
                view.task_item_responsible?.text = item.responsible

                view.setOnClickListener(editTaskItem(item))
            }
        }
    }
}