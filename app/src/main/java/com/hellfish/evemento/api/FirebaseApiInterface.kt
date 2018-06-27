package com.hellfish.evemento.api

import retrofit2.Call
import retrofit2.http.*

//https://deep-hook-204120.firebaseio.com/events.json?orderBy=%22user%22&equalTo=%22j4uR2B9QbWPEJdJ1E99LSFZa9HD3%22
//https://deep-hook-204120.firebaseio.com/events.json?orderBy=%22user%22&equalTo=%22AwrjKTnQ5CTfmfLEMxvmEmkM6Tz2%22
//https://deep-hook-204120.firebaseio.com/users.json
interface  FirebaseApiInterface {


    //
    // USERS
    //

    //https://deep-hook-204120.firebaseio.com/users/fiSgMBbaYddmJdAu7cHsRUiOglU2.json
    @GET("/users/{userId}.json")
    fun getUser(@Path("userId") userId: String)
            : Call<UserResponse>

    @Headers("Content-Type: application/json")
    @PATCH("/users/{userId}.json")
    fun createOrUpdateUser(@Path("userId") userId: String,
                           @Body user: UserPartialResponse)
            : Call<UserResponse>

    //
    // POLLS
    //


    //https://deep-hook-204120.firebaseio.com/polls.json?orderBy=%22eventId%22&equalTo=%22-LFkNSwG9kj9Ytw_5mXa%22
    @GET("/polls.json")
    fun getPolls(@Query("orderBy") orderBy: String,
                 @Query("equalTo") equalTo: String)
            : Call<Map<String, PollResponse>>

    @Headers("Content-Type: application/json")
    @POST("/polls.json")
    fun pushPoll(@Body poll: PollResponse)
            : Call<PushResponse>

    @DELETE("/polls/{pollId}.json")
    fun deletePoll(@Path("pollId") pollId: String)
            : Call<DeleteResponse>

    //
    // COMMENTS
    //

    //https://deep-hook-204120.firebaseio.com/comments.json?orderBy=%22eventId%22&equalTo=%22-LFkNSwG9kj9Ytw_5mXa%22
    @GET("/comments.json")
    fun getComments(@Query("orderBy") orderBy: String,
                    @Query("equalTo") equalTo: String)
            : Call<Map<String, CommentResponse>>

    @Headers("Content-Type: application/json")
    @POST("/comments.json")
    fun pushComment(@Body comment: CommentResponse)
            : Call<PushResponse>

    @DELETE("/comments/{commentId}.json")
    fun deleteComment(@Path("commentId") commentId: String)
            : Call<DeleteResponse>


    //
    // EVENTS
    //

    @GET("/events.json")
    fun getEvents(@Query("orderBy") orderBy: String,
                  @Query("equalTo") equalTo: String)
            : Call<Map<String, EventResponse>>

    @Headers("Content-Type: application/json")
    @POST("/events.json")
    fun pushEvent(@Body event: EventResponse)
            : Call<PushResponse>

    @Headers("Content-Type: application/json")
    @PUT("/events/{eventId}.json")
    fun updateEvent(@Path("eventId") eventId: String,
                    @Body event: EventResponse)
            : Call<EventResponse>

}