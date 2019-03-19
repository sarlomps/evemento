package com.sarlomps.evemento.api

import com.sarlomps.evemento.R
import com.sarlomps.evemento.event.Event
import com.sarlomps.evemento.event.poll.Poll
import com.sarlomps.evemento.event.task.TaskItem
import com.sarlomps.evemento.event.transport.TransportItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestAPI {

    private val firebaseApi: FirebaseApiInterface

    init {
        val apiClient = FirebaseApiClient.client
        firebaseApi = apiClient.create(FirebaseApiInterface::class.java)
    }

    private fun pushCallback(callback: (String?, Int?) -> Unit) = object : Callback<PushResponse> {
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
    }
    private fun deleteCallback(callback: (Boolean, Int?) -> Unit) = object : Callback<DeleteResponse> {
        override fun onResponse(call: Call<DeleteResponse>?, response: Response<DeleteResponse>?) {
            if (response != null && response.isSuccessful) {
                callback(true, null)
                return
            }
            callback(false, R.string.api_error_deleting_data)
        }

        override fun onFailure(call: Call<DeleteResponse>?, t: Throwable?) {
            callback(false, R.string.api_error_deleting_data)
        }
    }
    private fun <E, D>updateCallback(callback: (D?, Int?) -> Unit, mapper: Mapper<E, D>, id: String): Callback<E> = object : Callback<E> {
        override fun onResponse(call: Call<E>?, response: Response<E>?) {
            if (response != null && response.isSuccessful) {
                response.body()?.let {
                    callback(mapper.mapToDomain(id, it), null)
                    return
                }
            }
            callback(null, R.string.api_error_pushing_data)
        }

        override fun onFailure(call: Call<E>?, t: Throwable?) {
            callback(null, R.string.api_error_pushing_data)
        }
    }
    private fun <E, D>getXForYCallback(callback: (List<D>?, Int?) -> (Unit), mapper: Mapper<E, D>) = object : Callback<Map<String, E>> {
        override fun onResponse(call: Call<Map<String, E>>?, response: Response<Map<String, E>>?) {
            if (response != null && response.isSuccessful) {
                response.body()?.let {
                    callback(it.map {entry -> mapper.mapToDomain(entry.key, entry.value)}, null)
                    return
                }
            }
            callback(null, R.string.api_error_fetching_data)
        }

        override fun onFailure(call: Call<Map<String, E>>?, t: Throwable?) {
            callback(null, R.string.api_error_fetching_data)
        }
    }

    //Events
    //https://deep-hook-204120.firebaseio.com/events.json?orderBy=%22user%22&equalTo=%22AwrjKTnQ5CTfmfLEMxvmEmkM6Tz2%22
    fun getEventsForUser(user: String, callback: (List<Event>?, Int?) -> (Unit)) {
        firebaseApi.getEvents("\"user\"", "\"$user\"").enqueue(getXForYCallback(callback, EventMapper()))
    }

    fun getInvitations(user: String, callback: (List<String>?, Int?) -> (Unit)) {
        firebaseApi.getGuests("\"userId\"", "\"$user\"").enqueue(object : Callback<Map<String, GuestResponse>> {
            override fun onResponse(call: Call<Map<String, GuestResponse>>?, response: Response<Map<String, GuestResponse>>?) {
                if (response != null && response.isSuccessful) {
                    response.body()?.let {
                        callback(it.values.map { it.eventId }, null)
                        return
                    }
                }
                callback(null, R.string.api_error_fetching_data)
            }

            override fun onFailure(call: Call<Map<String, GuestResponse>>?, t: Throwable?) {
                callback(null, R.string.api_error_fetching_data)
            }
        })
    }

    fun getEvent(eventId:String, callback: (Event?, Int?) -> Unit) {
        firebaseApi.getEvent(eventId).enqueue(updateCallback(callback, EventMapper(), eventId))
    }

    fun pushEvent(event:EventResponse, callback: (String?, Int?) -> Unit) {
        firebaseApi.pushEvent(event).enqueue(pushCallback(callback))
    }

    fun updateEvent(eventId:String, event:EventResponse, callback: (Event?, Int?) -> Unit) {
        firebaseApi.updateEvent(eventId, event).enqueue(updateCallback(callback, EventMapper(), eventId))
    }

    fun deleteEvent(eventId: String, callback: (Boolean, Int?) -> Unit) {
        var error: Int? = null
        deleteAllComments(eventId) { _, errorMessage -> error = errorMessage }
        deleteAllPolls(eventId) { _, errorMessage -> error = errorMessage }
        deleteAllGuests(eventId) { _, errorMessage -> error = errorMessage }
        deleteAllTransports(eventId) { _, errorMessage -> error = errorMessage }
        deleteAllTasks(eventId) { _, errorMessage -> error = errorMessage }

        if (error == null) {
            firebaseApi.deleteEvent(eventId).enqueue(deleteCallback(callback))
        }
        else callback(false, error)
    }

    private fun deleteAllComments(eventId: String, callback: (Boolean, Int?) -> (Unit)) {
        getCommentsForEvent(eventId) { comments, error ->
            if (comments != null) comments.forEach { deleteComment(it.commentId, callback) }
            else callback(false, error)
        }
    }

    private fun deleteAllPolls(eventId: String, callback: (Boolean, Int?) -> (Unit)) {
        getPollsForEvent(eventId) { polls, error ->
            if ( polls != null) polls.forEach { deletePoll(it.pollId, callback) }
            else callback(false, error)
        }
    }

    private fun deleteAllGuests(eventId: String, callback: (Boolean, Int?) -> (Unit)) {
        getGuestsForEvent(eventId) { guests, error ->
            if (guests != null) guests.forEach { deleteGuest(it.guestId, callback) }
            else callback(false, error)
        }
    }

    private fun deleteAllTransports(eventId: String, callback: (Boolean, Int?) -> (Unit)) {
        getTransportsForEvent(eventId) { transports, error ->
            if (transports != null) transports.forEach { deleteGuest(it.transportId, callback) }
            else callback(false, error)
        }
    }

    private fun deleteAllTasks(eventId: String, callback: (Boolean, Int?) -> (Unit)) {
        getTasksForEvent(eventId) { tasks, error ->
            if (tasks != null) tasks.forEach { deleteGuest(it.taskId, callback) }
            else callback(false, error)
        }
    }

    //Users
    fun createOrUpdateUser(userId: String, user:UserResponse, callback: (User?, Int?) -> (Unit)) {
        createOrUpdateUser(userId, UserMapper().mapToPartialEntity(user), callback)
    }
    fun createOrUpdateUser(userId: String, user:UserPartialResponse, callback: (User?, Int?) -> (Unit)) {
        firebaseApi.createOrUpdateUser(userId, user).enqueue(updateCallback(callback, UserMapper(), userId))
    }

    fun getUser(userId: String, callback: (User?, Int?) -> (Unit)) {
        firebaseApi.getUser(userId).enqueue(object : Callback<UserResponse> {
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

    fun getAllUsers(callback: (List<User>?, Int?) -> (Unit)) {
        firebaseApi.getAllUsers().enqueue(object : Callback<Map<String, UserResponse>> {
            override fun onResponse(call: Call<Map<String, UserResponse>>?, response: Response<Map<String, UserResponse>>?) {
                if (response != null && response.isSuccessful) {
                    response.body()?.let {
                        callback(it.map { entry -> UserMapper().mapToDomain(entry.key, entry.value)}, null)
                        return
                    }
                }
                callback(null, R.string.api_error_fetching_data)
            }

            override fun onFailure(call: Call<Map<String, UserResponse>>?, t: Throwable?) {
                callback(null, R.string.api_error_fetching_data)
            }
        })
    }

    //Polls
    //https://deep-hook-204120.firebaseio.com/polls.json?orderBy=%22eventId%22&equalTo=%22-LFkNSwG9kj9Ytw_5mXa%22
    fun getPollsForEvent(eventId: String, callback: (List<Poll>?, Int?) -> (Unit)) {
        firebaseApi.getPolls("\"eventId\"", "\"$eventId\"").enqueue(getXForYCallback(callback, PollMapper()))
    }

    fun pushPoll(poll: PollResponse, callback: (String?, Int?) -> Unit) {
        firebaseApi.pushPoll(poll).enqueue(pushCallback(callback))
    }

    fun updatePoll(pollId: String, poll: PollResponse, callback: (Poll?, Int?) -> Unit) {
        firebaseApi.updatePoll(pollId, poll).enqueue(updateCallback(callback, PollMapper(), pollId))
    }

    fun deletePoll(pollId: String, callback: (Boolean, Int?) -> Unit) {
        firebaseApi.deletePoll(pollId).enqueue(deleteCallback(callback))
    }

    //Comments
    //https://deep-hook-204120.firebaseio.com/polls.json?orderBy=%22eventId%22&equalTo=%22-LFkNSwG9kj9Ytw_5mXa%22
    fun getCommentsForEvent(eventId: String, callback: (List<Comment>?, Int?) -> (Unit)) {
        firebaseApi.getComments("\"eventId\"", "\"$eventId\"").enqueue(getXForYCallback(callback, CommentMapper()))
    }

    fun pushComment(comment: CommentResponse, callback: (String?, Int?) -> Unit) {
        firebaseApi.pushComment(comment).enqueue(pushCallback(callback))
    }

    fun updateComment(commentId: String, comment: CommentResponse, callback: (Comment?, Int?) -> Unit) {
        firebaseApi.updateComment(commentId, comment).enqueue(updateCallback(callback, CommentMapper(), commentId))
    }

    fun deleteComment(commentId: String, callback: (Boolean, Int?) -> Unit) {
        firebaseApi.deleteComment(commentId).enqueue(deleteCallback(callback))
    }

    //Guest
    fun getGuestsForEvent(eventId: String, callback: (List<Guest>?, Int?) -> (Unit)) {
        firebaseApi.getGuests("\"eventId\"", "\"$eventId\"").enqueue(getXForYCallback(callback, GuestMapper()))
    }

    fun pushGuest(guest: GuestResponse, callback: (String?, Int?) -> Unit) {
        firebaseApi.pushGuest(guest).enqueue(pushCallback(callback))
    }

    fun deleteGuest(guestId: String, callback: (Boolean, Int?) -> Unit) {
        firebaseApi.deleteGuest(guestId).enqueue(deleteCallback(callback))
    }

    //Transports
    fun getTransportsForEvent(eventId: String, callback: (List<TransportItem>?, Int?) -> (Unit)) {
        firebaseApi.getTransports("\"eventId\"", "\"$eventId\"").enqueue(getXForYCallback(callback, TransportMapper()))
    }

    fun pushTransport(transport: TransportResponse, callback: (String?, Int?) -> Unit) {
        firebaseApi.pushTransport(transport).enqueue(pushCallback(callback))
    }

    fun updateTransport(transportId: String, transport: TransportResponse, callback: (TransportItem?, Int?) -> Unit) {
        firebaseApi.updateTransport(transportId, transport).enqueue(updateCallback(callback, TransportMapper(), transportId))
    }

    fun deleteTransport(transportId: String, callback: (Boolean, Int?) -> Unit) {
        firebaseApi.deleteTransport(transportId).enqueue(deleteCallback(callback))
    }

    //Tasks
    fun getTasksForEvent(eventId: String, callback: (List<TaskItem>?, Int?) -> (Unit)) {
        firebaseApi.getTasks("\"eventId\"", "\"$eventId\"").enqueue(getXForYCallback(callback, TaskMapper()))
    }

    fun pushTask(comment: TaskResponse, callback: (String?, Int?) -> Unit) {
        firebaseApi.pushTask(comment).enqueue(pushCallback(callback))
    }

    fun updateTask(commentId: String, comment: TaskResponse, callback: (TaskItem?, Int?) -> Unit) {
        firebaseApi.updateTask(commentId, comment).enqueue(updateCallback(callback, TaskMapper(), commentId))
    }

    fun deleteTask(commentId: String, callback: (Boolean, Int?) -> Unit) {
        firebaseApi.deleteTask(commentId).enqueue(deleteCallback(callback))
    }

}