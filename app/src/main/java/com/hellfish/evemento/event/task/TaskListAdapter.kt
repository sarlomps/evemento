package com.hellfish.evemento.event.task

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import com.hellfish.evemento.R
import com.hellfish.evemento.RecyclerAdapter
import kotlinx.android.synthetic.main.fragment_task_list_item.view.*

class TaskListAdapter(private val taskItems: MutableList<TaskItem>) : RecyclerAdapter<CardView, TaskItem?>(taskItems) {
    override fun layout(item: Int): Int {
        return R.layout.fragment_task_list_item
    }

    override fun doOnItemOnBindViewHolder(view: CardView, item: TaskItem?, context: Context) {
        view.task_item?.text = item?.description
        view.task_item_owner?.text = item?.owner
    }

    override fun getItemCount(): Int {
        return taskItems.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }

}