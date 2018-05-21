package com.hellfish.evemento

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class RecyclerAdapter<V: View,
                                    Item>(open val items: List<Item>) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder<V>>() {

    private var context : Context? = null

    class ViewHolder<out V : View>(val view: V) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<V> {
        val view = LayoutInflater.from(parent.context)
                .inflate(layout(), parent, false) as V

        this.context = parent.context

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder<V>, position: Int) : Unit {
        this.context?.let {
            context -> doOnItemOnBindViewHolder(holder.view, items[position], context)
        }
    }

    abstract fun layout() : Int

    abstract fun doOnItemOnBindViewHolder(view: V, item: Item, context: Context) : Unit
}
