package com.hellfish.evemento.api

import com.hellfish.evemento.R
import com.hellfish.evemento.event.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestAPI {

    private val firebaseApi: FirebaseApiInterface

    init {
        val apiClient = FirebaseApiClient.client


        firebaseApi = apiClient.create(FirebaseApiInterface::class.java)
    }

    private fun getEventsCall(orderBy: String, equalTo: String): Call<Map<String, EventResponse>> {
        return firebaseApi.getEvents(orderBy, equalTo)
    }

    //https://deep-hook-204120.firebaseio.com/events.json?orderBy=%22user%22&equalTo=%22AwrjKTnQ5CTfmfLEMxvmEmkM6Tz2%22
    fun getEventsForUser(user: String, callback: (List<Event>?, Int?) -> (Unit)) {
        val call = getEventsCall("\"user\"", "\"$user\"")
        call.enqueue(object : Callback<Map<String, EventResponse>> {
            override fun onResponse(call: Call<Map<String, EventResponse>>?, response: Response<Map<String, EventResponse>>?) {
                if (response != null && response.isSuccessful) {
                    response.body()?.let {
                        callback(it.map {entry -> EventMapper().mapToDomain(entry.key, entry.value)}, null)
                        return
                    }
                }
                callback(null, R.string.api_error_fetching_data)
            }

            override fun onFailure(call: Call<Map<String, EventResponse>>?, t: Throwable?) {
                callback(null, R.string.api_error_fetching_data)
            }
        })
    }

    private fun pushEventCall(event:EventResponse): Call<PushResponse> {
        return firebaseApi.pushEvent(event)
    }
    fun pushEvent(event:EventResponse, callback: (String?, Int?) -> Unit) {
        val call = pushEventCall(event)
        call.enqueue(object : Callback<PushResponse> {
            override fun onResponse(call: Call<PushResponse>?, response: Response<PushResponse>?) {
                if (response != null && response.isSuccessful) {
                    response.body()?.let {
                        callback(it.name, null)
                        return
                    }
                }
                callback(null, R.string.api_error_pushing_data)
            }

            override fun onFailure(call: Call<PushResponse>?, t: Throwable?) {
                callback(null, R.string.api_error_pushing_data)
            }
        })
    }

    private fun updateEventCall(eventId: String, event:EventResponse): Call<EventResponse> {
        return firebaseApi.updateEvent(eventId, event)
    }
    fun updateEvent(eventId:String, event:EventResponse, callback: (Event?, Int?) -> Unit) {
        val call = updateEventCall(eventId, event)
        call.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>?, response: Response<EventResponse>?) {
                if (response != null && response.isSuccessful) {
                    response.body()?.let {
                        callback(EventMapper().mapToDomain(eventId, it), null)
                        return
                    }
                }
                callback(null, R.string.api_error_pushing_data)
            }

            override fun onFailure(call: Call<EventResponse>?, t: Throwable?) {
                callback(null, R.string.api_error_pushing_data)
            }
        })
    }


    private fun getCreateOrUpdateUserCall(userId: String, user:UserPartialResponse): Call<UserResponse> {
        return firebaseApi.createOrUpdateUser(userId, user)
    }
    fun createOrUpdateUser(userId: String, user:UserResponse, callback: (User?, Int?) -> (Unit)) {
        createOrUpdateUser(userId, UserMapper().mapToPartialEntity(user), callback)
    }
    fun createOrUpdateUser(userId: String, user:UserPartialResponse, callback: (User?, Int?) -> (Unit)) {
        val call = getCreateOrUpdateUserCall(userId, user)
        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>?, response: Response<UserResponse>?) {
                if (response != null && response.isSuccessful) {
                    response.body()?.let {
                        callback(UserMapper().mapToDomain(userId, it), null)
                        return
                    }
                }
                callback(null, R.string.api_error_fetching_data)
            }

            override fun onFailure(call: Call<UserResponse>?, t: Throwable?) {
                callback(null, R.string.api_error_fetching_data)
            }
        })
    }

    private fun getUserCall(userId: String): Call<UserResponse> {
        return firebaseApi.getUser(userId)
    }
    fun getUser(userId: String, callback: (User?, Int?) -> (Unit)) {
        val call = getUserCall(userId)
        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>?, response: Response<UserResponse>?) {
                if (response != null && response.isSuccessful) {
                    response.body()?.let {
                        callback(UserMapper().mapToDomain(userId, it), null)
                        return
                    }
                }
                callback(null, R.string.api_error_fetching_data)
            }

            override fun onFailure(call: Call<UserResponse>?, t: Throwable?) {
                callback(null, R.string.api_error_fetching_data)
            }
        })
    }

    private fun getPollsForEventCall(orderBy: String, equalTo: String): Call<Map<String, PollResponse>> {
        return firebaseApi.getPolls(orderBy, equalTo)
    }

    //https://deep-hook-204120.firebaseio.com/polls.json?orderBy=%22eventId%22&equalTo=%22-LFkNSwG9kj9Ytw_5mXa%22
    fun getPollsForEvent(eventId: String, callback: (List<Poll>?, Int?) -> (Unit)) {
        val call = getPollsForEventCall("\"eventId\"", "\"$eventId\"")
        call.enqueue(object : Callback<Map<String, PollResponse>> {
            override fun onResponse(call: Call<Map<String, PollResponse>>?, response: Response<Map<String, PollResponse>>?) {
                if (response != null && response.isSuccessful) {
                    response.body()?.let {
                        callback(it.map {entry -> PollMapper().mapToDomain(entry.key, entry.value)}, null)
                        return
                    }
                }
                callback(null, R.string.api_error_fetching_data)
            }

            override fun onFailure(call: Call<Map<String, PollResponse>>?, t: Throwable?) {
                callback(null, R.string.api_error_fetching_data)
            }
        })
    }


    private fun pushPollCall(poll: PollResponse): Call<PushResponse> {
        return firebaseApi.pushPoll(poll)
    }
    fun pushPoll(poll: PollResponse, callback: (String?, Int?) -> Unit) {
        val call = pushPollCall(poll)
        call.enqueue(object : Callback<PushResponse> {
            override fun onResponse(call: Call<PushResponse>?, response: Response<PushResponse>?) {
                if (response != null && response.isSuccessful) {
                    response.body()?.let {
                        callback(it.name, null)
                        return
                    }
                }
                callback(null, R.string.api_error_pushing_data)
            }

            override fun onFailure(call: Call<PushResponse>?, t: Throwable?) {
                callback(null, R.string.api_error_pushing_data)
            }
        })
    }




    private fun getCommentsForEventCall(orderBy: String, equalTo: String): Call<Map<String, CommentResponse>> {
        return firebaseApi.getComments(orderBy, equalTo)
    }

    //https://deep-hook-204120.firebaseio.com/polls.json?orderBy=%22eventId%22&equalTo=%22-LFkNSwG9kj9Ytw_5mXa%22
    fun getCommentsForEvent(eventId: String, callback: (List<Comment>?, Int?) -> (Unit)) {
        val call = getCommentsForEventCall("\"eventId\"", "\"$eventId\"")
        call.enqueue(object : Callback<Map<String, CommentResponse>> {
            override fun onResponse(call: Call<Map<String, CommentResponse>>?, response: Response<Map<String, CommentResponse>>?) {
                if (response != null && response.isSuccessful) {
                    response.body()?.let {
                        callback(it.map {entry -> CommentMapper().mapToDomain(entry.key, entry.value)}, null)
                        return
                    }
                }
                callback(null, R.string.api_error_fetching_data)
            }

            override fun onFailure(call: Call<Map<String, CommentResponse>>?, t: Throwable?) {
                callback(null, R.string.api_error_fetching_data)
            }
        })
    }

    private fun pushCommentCall(comment: CommentResponse): Call<PushResponse> {
        return firebaseApi.pushComment(comment)
    }
    fun pushComment(comment: CommentResponse, callback: (String?, Int?) -> Unit) {
        val call = pushCommentCall(comment)
        call.enqueue(object : Callback<PushResponse> {
            override fun onResponse(call: Call<PushResponse>?, response: Response<PushResponse>?) {
                if (response != null && response.isSuccessful) {
                    response.body()?.let {
                        callback(it.name, null)
                        return
                    }
                }
                callback(null, R.string.api_error_pushing_data)
            }

            override fun onFailure(call: Call<PushResponse>?, t: Throwable?) {
                callback(null, R.string.api_error_pushing_data)
            }
        })
    }

}