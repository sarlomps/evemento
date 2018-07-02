package com.hellfish.evemento

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.hellfish.evemento.extensions.inflate
import android.widget.TextView


abstract class RecyclerAdapter<V: View,
                               Item>(val items: List<Item>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val EMPTY_VIEW = 10
    }

    private var context : Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder = when (viewType) {
            EMPTY_VIEW -> {

                val view = parent.inflate(layout(viewType), false) as TextView
                EmptyViewHolder<Item>(view)
            }
            else -> ContentViewHolder<V,Item>(parent.inflate(layout(viewType), false) as V)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (this.getItemViewType(position)) {
            EMPTY_VIEW -> (holder as EvementoViewHolder<TextView,Item>).doOnBind(context=context, action = doOnEmptyOnBindViewHolder())
            else -> (holder as EvementoViewHolder<V,Item>).doOnBind(item=items[position], context = context, action = doOnItemOnBindViewHolder())
        }
    }

    override fun getItemCount(): Int {
        return if (items.count() > 0) items.count() else 1
    }

    override fun getItemViewType(position: Int): Int {

        return if (items.count() == 0)
            EMPTY_VIEW
        else
            super.getItemViewType(position)
    }

    open fun getItemView(item: Item) : Int = 0

    abstract fun layout(item : Int) : Int

    abstract fun doOnItemOnBindViewHolder(): (view: V, item: Item, context: Context?) -> Unit
    abstract fun doOnEmptyOnBindViewHolder(): (view: TextView, context: Context?) -> Unit

    interface EvementoViewHolder<V:View,Item> {

        fun doOnBind(item: Item, context: Context?, action: (view: V, item: Item, context: Context?) -> Unit)
        fun doOnBind(context: Context?, action: (view: V, context: Context?) -> Unit)

    }
    class EmptyViewHolder<Item>(private val view: TextView)
        : RecyclerView.ViewHolder(view), EvementoViewHolder<TextView, Item> {
        override fun doOnBind(item: Item, context: Context?, action: (view: TextView, item: Item, context: Context?) -> Unit) {
            action(view, item, context)
        }
        override fun doOnBind(context: Context?, action: (view: TextView, context: Context?) -> Unit) {
            action(view, context)
        }
    }
    class ContentViewHolder<V:View, Item>(private val view: V)
        : RecyclerView.ViewHolder(view), EvementoViewHolder<V,Item> {
        override fun doOnBind(item: Item, context: Context?, action: (view: V, item: Item, context: Context?) -> Unit) {
            action(view, item, context)
        }
        override fun doOnBind(context: Context?, action: (view: V, context: Context?) -> Unit) {
            action(view, context)
        }
    }

}


