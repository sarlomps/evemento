package com.hellfish.evemento;

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class CarsAdapter(val carList:ArrayList<CarItem>): RecyclerView.Adapter<CarsAdapter.ViewHolder>(){
    override fun onBindViewHolder(holder:ViewHolder,position:Int){
        holder.txtDriverName.text=carList[position].driver
        holder.txtAvailableSlots.text=carList[position].availableSlots

    }

    override fun onCreateViewHolder(parent:ViewGroup,viewType:Int):ViewHolder{
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.car_item,parent,false))
    }

    override fun getItemCount():Int{
        return carList.size
    }

    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val txtDriverName=itemView.findViewById<TextView>(R.id.txtDriverName)
        val txtAvailableSlots=itemView.findViewById<TextView>(R.id.txtAvailableSlots)
    }

}
