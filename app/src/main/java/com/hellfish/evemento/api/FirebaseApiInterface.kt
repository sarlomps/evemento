package com.hellfish.evemento.api

import retrofit2.Call
import retrofit2.http.*

//https://deep-hook-204120.firebaseio.com/events.json?orderBy=%22user%22&equalTo=%22j4uR2B9QbWPEJdJ1E99LSFZa9HD3%22
//https://deep-hook-204120.firebaseio.com/events.json?orderBy=%22user%22&equalTo=%22AwrjKTnQ5CTfmfLEMxvmEmkM6Tz2%22
interface  FirebaseApiInterface {
    @GET("/events.json")
    fun getEvents(@Query("orderBy") orderBy: String,
               @Query("equalTo") equalTo: String)
            : Call<Map<String, EventResponse>>

    @Headers("Content-Type: application/json")
    @POST("/events.json")
    fun pushEvent(@Field("title") title: String,
                  @Field("description") description: String,
                  @Field("startDate") startDate: String,
                  @Field("endDate") endDate: String,
                  @Field("location") location: String,
                  @Field("user") user: String)
            : Call<PushEventResponse>



}