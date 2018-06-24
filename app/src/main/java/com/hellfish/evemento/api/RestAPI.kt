package com.hellfish.evemento.api

import com.hellfish.evemento.R
import com.hellfish.evemento.event.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestAPI {

    private val eventsApi: FirebaseApiInterface

    init {
        val apiClient = FirebaseApiClient.client


        eventsApi = apiClient.create(FirebaseApiInterface::class.java)
    }

    fun getEventsCall(orderBy: String, equalTo: String): Call<Map<String, EventResponse>> {
        return eventsApi.getEvents(orderBy, equalTo)
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
}