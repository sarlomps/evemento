package com.hellfish.evemento.api

import com.google.firebase.auth.FirebaseUser
import com.hellfish.evemento.event.Event
import com.hellfish.evemento.event.task.TaskItem
import com.hellfish.evemento.event.transport.TransportItem
import org.joda.time.DateTime

data class EventResponse(val title: String,
                         val imageUrl: String,
                         val description: String,
                         val startDate: String,
                         val endDate: String,
                         val location: String,
                         val user: String)

data class UserResponse(val displayName: String,
                        val imageUrl: String?,
                        val email: String)

data class UserPartialResponse(val displayName: String?,
                               val imageUrl: String?,
                               val email: String?)

data class User(val userId: String,
                val displayName: String,
                val imageUrl: String?,
                val email: String)


data class Comment(val commentsId:String,
                    val eventId:String,
                    val text: String,
                    val userId: String)

data class Poll(val pollId: String,
                val eventId: String,
                val items: Map<String,Map<String,Map<String,Boolean>>>) // {pregunta:{respuesta:{usuario:true}}}

/// Push new event response
data class PushEventResponse(val name:String)
data class PollResponse(val eventId:String,
                        val items: Map<String,Map<String,Map<String,Boolean>>>) // {pregunta:{respuesta:{usuario:true}}}
data class CommentResponse(val eventId:String,
                           val text: String,
                           val userId: String)

class UserMapper {

    fun mapToDomain(userId: String, entity: UserResponse): User {

        return User(
                userId,
                entity.displayName,
                entity.imageUrl,
                entity.email)
    }

    fun mapToPartialEntity(user: FirebaseUser): UserPartialResponse = UserPartialResponse(
            user.displayName,
            null,
            user.email)

    fun mapToPartialEntity(user: UserResponse): UserPartialResponse = UserPartialResponse(
            user.displayName,
            user.imageUrl,
            user.email)

    fun mapToEntity(user: User): UserResponse = UserResponse(
            user.displayName,
            user.imageUrl,
            user.email)
}

class EventMapper {

    fun mapToDomain(eventId: String, entity: EventResponse): Event = Event(
            eventId,
            entity.title,
            entity.imageUrl,
            entity.description,
            DateTime.parse(entity.startDate),
            DateTime.parse(entity.endDate),
            entity.location,
            entity.user)

    fun mapToEntity(event: Event): EventResponse = EventResponse(
            event.title,
            event.imageUrl,
            event.description,
            event.startDate.toString(),
            event.endDate.toString(),
            event.location,
            event.user)

}

class PollMapper {
    fun mapToDomain(pollId: String, entity: PollResponse): Poll = Poll(
            pollId,
            entity.eventId,
            entity.items)

    fun mapToEntity(poll: Poll): PollResponse = PollResponse(
            poll.eventId,
            poll.items)
}

class CommentMapper {
    fun mapToDomain(commentId: String, entity: CommentResponse): Comment = Comment(
            commentId,
            entity.eventId,
            entity.text,
            entity.userId)

    fun mapToEntity(comment: Comment): CommentResponse = CommentResponse(
            comment.eventId,
            comment.text,
            comment.userId)

}