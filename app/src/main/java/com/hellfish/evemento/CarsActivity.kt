package com.hellfish.evemento

import android.os.Bundle
import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_cars.*

class CarsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cars)

        var recycler: RecyclerView = findViewById(R.id.carsRecyclerView)
        recycler.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        val cars = ArrayList<CarItem>()
        cars.add(CarItem("Gus", "1"))
        cars.add(CarItem("Gas", "0"))

        var adapter = CarsAdapter(cars)
        recycler.adapter = adapter
    }
}