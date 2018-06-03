package com.hellfish.evemento

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.hellfish.evemento.extensions.inflate

abstract class RecyclerAdapter<V: View,
                               Item>(val items: List<Item>) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder<V>>() {

    private var context : Context? = null

    class ViewHolder<out V : View>(val view: V) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<V> {
        this.context = parent.context

        val view = parent.inflate(layout(viewType), false) as V

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

    override fun getItemViewType(position: Int): Int = getItemView(items[position])

    open fun getItemView(item: Item) : Int = 0

    abstract fun layout(item : Int) : Int

    abstract fun doOnItemOnBindViewHolder(view: V, item: Item, context: Context) : Unit
}
