package com.hellfish.evemento
import com.hellfish.evemento.api.*
import com.hellfish.evemento.event.Event

object NetworkManager {
    
    private val api: RestAPI = RestAPI()

    /// USERS
    fun getUser(userId: String, callback: (User?, Int?) -> (Unit)) {
        api.getUser(userId, callback)
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

    fun pushEvent(event:Event, callback: (String?, Int?) -> (Unit)) {
        api.pushEvent(EventMapper().mapToEntity(event), callback)
    }
    // EJEMPLO DE COMO SE USA:
    // PUSHEA AL SERVER Y RETORNA EL NUEVO ID DEL EVENTO QUE LE OTORGO FIREBASE
//    NetworkManager.pushEvent(event) { name, errorMessage ->
//        name?.let {
//            Log.d("pushEvent, new id:", it)
//            event.eventId = it
//            return@pushEvent
//        }
//        showSnackbar(errorMessage ?: R.string.network_unknown_error, main_container)
//    }

    fun updateEvent(event:Event, callback: (Event?, Int?) -> (Unit)) {
        api.updateEvent(event.eventId, EventMapper().mapToEntity(event), callback)
    }
    // EJEMPLO DE COMO SE USA:
    // UPDATEA EL EVENTO AL SERVER Y RETORNA EL EVENTO QUE SE ACABA DE UPDATEAR A FIREBASE
//    NetworkManager.updateEvent(event) { updatedEvent, errorMessage ->
//        updatedEvent?.let {
//            Log.d("updatedEvent:", it)
//            return@updateEvent
//        }
//        showSnackbar(errorMessage ?: R.string.network_unknown_error, main_container)
//    }

    /// POLLS
    fun getPolls(event:Event, callback: (List<Poll>?, Int?) -> (Unit)) {
        api.getPollsForEvent(event.eventId, callback)
    }
    fun pushPolls(poll: Poll, callback: (String?, Int?) -> (Unit)) {
        api.pushPoll(PollMapper().mapToEntity(poll), callback)
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

    // EJEMPLO DE COMO SE USA:
    // BORRA EL COMMENT DEL SERVER
//    NetworkManager.deleteComment(comment) { success, errorMessage ->
//        if (success) {
//            // hacer algo y avisar al usuario que se borro bien
//            return@deleteComment
//        }
//        showSnackbar(errorMessage ?: R.string.network_unknown_error, main_container)
//    }

}