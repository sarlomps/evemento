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
    fun pushEvent(@Body event: EventResponse)
            : Call<PushEventResponse>

    @Headers("Content-Type: application/json")
    @PUT("/events/{eventId}.json")
    fun updateEvent(@Path("eventId") eventId: String,
                    @Body event: EventResponse)
            : Call<EventResponse>
}