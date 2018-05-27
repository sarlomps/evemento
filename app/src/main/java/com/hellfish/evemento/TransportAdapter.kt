package com.hellfish.evemento;

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class TransportAdapter(val transportList:ArrayList<TransportItem>): RecyclerView.Adapter<TransportAdapter.ViewHolder>(){
    override fun onBindViewHolder(holder:ViewHolder,position:Int){
        holder.txtDriverName.text=transportList[position].driver
        holder.txtAvailableSlots.text=transportList[position].availableSlots

    }

    override fun onCreateViewHolder(parent:ViewGroup,viewType:Int):ViewHolder{
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.transport_item,parent,false))
    }

    override fun getItemCount():Int{
        return transportList.size
    }

    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val txtDriverName=itemView.findViewById<TextView>(R.id.txtDriverName)
        val txtAvailableSlots=itemView.findViewById<TextView>(R.id.txtAvailableSlots)
    }

}
