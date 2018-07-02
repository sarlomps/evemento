package com.hellfish.evemento
import android.media.Image
import com.hellfish.evemento.api.*
import com.hellfish.evemento.event.Event
import com.hellfish.evemento.event.poll.Poll
import com.hellfish.evemento.event.task.TaskItem
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

import com.hellfish.evemento.event.transport.TransportItem

object NetworkManager {
    
    private val api: RestAPI = RestAPI()
    private val imageApi: ImageAPI = ImageAPI()

    /// USERS
    fun getUser(userId: String, callback: (User?, Int?) -> (Unit)) {
        api.getUser(userId, callback)
    }
    fun getAllUsers(callback: (List<User>?, Int?) -> (Unit)) {
        api.getAllUsers(callback)
    }
    fun updateUser(userId: String, user:UserPartialResponse, callback: (User?, Int?) -> (Unit)) {
        api.createOrUpdateUser(userId, user, callback)
    }
    fun updateUser(userId: String, user:User, callback: (User?, Int?) -> (Unit)) {
        api.createOrUpdateUser(userId, UserMapper().mapToEntity(user), callback)
    }

    /// EVENTS
    fun getEventsForUser(user: String, callback: (List<Event>?, Int?) -> (Unit)) {
        api.getEventsForUser(user, callback)
    }

    fun getInvitations(user: String, callback: (List<String>?, Int?) -> (Unit)) {
        api.getInvitations(user, callback)
    }

    fun getEvent(event: String, callback: (Event?, Int?) -> (Unit)) {
        api.getEvent(event, callback)
    }

    fun pushEvent(event:Event, callback: (String?, Int?) -> (Unit)) {
        api.pushEvent(EventMapper().mapToEntity(event), callback)
    }

    fun deleteEvent(eventId: String, callback: (Boolean, Int?) -> (Unit)) {
        api.deleteEvent(eventId, callback)
    }

    fun updateEvent(event:Event, callback: (Event?, Int?) -> (Unit)) {
        api.updateEvent(event.eventId, EventMapper().mapToEntity(event), callback)
    }

    /// POLLS
    fun getPolls(event:Event, callback: (List<Poll>?, Int?) -> (Unit)) {
        api.getPollsForEvent(event.eventId, callback)
    }
    fun pushPoll(poll: Poll, callback: (String?, Int?) -> (Unit)) {
        api.pushPoll(PollMapper().mapToEntity(poll), callback)
    }
    fun updatePoll(poll: Poll, callback: (Poll?, Int?) -> Unit) {
        api.updatePoll(poll.pollId, PollMapper().mapToEntity(poll), callback)
    }
    fun deletePoll(poll: Poll, callback: (Boolean, Int?) -> (Unit)) {
        api.deletePoll(poll.pollId, callback)
    }

    /// COMMENTS
    fun getComments(event:Event, callback: (List<Comment>?, Int?) -> (Unit)) {
        api.getCommentsForEvent(event.eventId, callback)
    }

    fun pushComment(eventId: String, comment: Comment, callback: (String?, Int?) -> (Unit)) {
        api.pushComment(CommentMapper().mapToEntity(eventId, comment), callback)
    }

    fun updateComment(eventId: String, comment: Comment, callback: (Comment?, Int?) -> (Unit)) {
        api.updateComment(comment.commentId, CommentMapper().mapToEntity(eventId, comment), callback)
    }

    fun deleteComment(comment: Comment, callback: (Boolean, Int?) -> (Unit)) {
        api.deleteComment(comment.commentId, callback)
    }

    /// GUESTS
    fun getGuests(event: Event, callback: (List<Guest>?, Int?) -> (Unit)) {
        api.getGuestsForEvent(event.eventId, callback)
    }

    fun pushGuest(eventId: String, guest: Guest, callback: (String?, Int?) -> (Unit)) {
        api.pushGuest(GuestMapper().mapToEntity(eventId, guest), callback)
    }

    fun deleteGuest(guest: Guest, callback: (Boolean, Int?) -> (Unit)) {
        api.deleteGuest(guest.guestId, callback)
    }

    /// IMAGES
    fun uploadImage(image: File, callback: (String?, Int?) -> (Unit)) {
        imageApi.uploadImage(image, callback)
    }

    fun cancelImageUpload() {
        imageApi.cancelCurrentCall()
    }

    /// Transport
    fun getTransports(event:Event, callback: (List<TransportItem>?, Int?) -> (Unit)) {
        api.getTransportsForEvent(event.eventId, callback)
    }

    fun pushTransport(eventId: String, transport: TransportItem, callback: (String?, Int?) -> (Unit)) {
        api.pushTransport(TransportMapper().mapToEntity(eventId, transport), callback)
    }

    fun updateTransport(eventId: String, transport: TransportItem, callback: (TransportItem?, Int?) -> (Unit)) {
        api.updateTransport(transport.transportId, TransportMapper().mapToEntity(eventId, transport)) { updated, error ->
            if(updated != null)
                NetworkManager.getUser(updated.driver.userId) { fullDriver, getUserError ->
                    fullDriver?.let { callback(updated.copy(driver = fullDriver), null); return@getUser }
                    callback(null, getUserError)
                }
            else callback(null, error)
        }
    }

    fun deleteTransport(transport: TransportItem, callback: (Boolean, Int?) -> (Unit)) {
        api.deleteTransport(transport.transportId, callback)
    }

    /// Tasks
    fun getTasks(event:Event, callback: (List<TaskItem>?, Int?) -> (Unit)) {
        api.getTasksForEvent(event.eventId, callback)
    }

    fun pushTask(eventId: String, task: TaskItem, callback: (String?, Int?) -> (Unit)) {
        api.pushTask(TaskMapper().mapToEntity(eventId, task), callback)
    }

    fun updateTask(eventId: String, task: TaskItem, callback: (TaskItem?, Int?) -> (Unit)) {
        api.updateTask(task.taskId, TaskMapper().mapToEntity(eventId, task), callback)
    }

    fun deleteTask(task: TaskItem, callback: (Boolean, Int?) -> (Unit)) {
        api.deleteTask(task.taskId, callback)
    }


}