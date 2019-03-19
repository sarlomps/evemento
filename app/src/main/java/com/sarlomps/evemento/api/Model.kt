package com.sarlomps.evemento.api

import com.google.firebase.auth.FirebaseUser
import com.sarlomps.evemento.SessionManager
import com.sarlomps.evemento.event.Event
import com.sarlomps.evemento.event.poll.Answer
import com.sarlomps.evemento.event.poll.Poll
import com.sarlomps.evemento.event.task.TaskItem
import com.sarlomps.evemento.event.transport.Coordinates
import com.sarlomps.evemento.event.transport.Location
import com.sarlomps.evemento.event.transport.TransportItem
import org.joda.time.DateTime

typealias UserIdResponse = String
typealias AnswerResponse = String

data class EventResponse(val title: String,
                         val imageUrl: String,
                         val description: String,
                         val startDate: String,
                         val endDate: String,
                         val location: String,
                         val latitude: String,
                         val longitude: String,
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
                val email: String) {
    fun sameUser(user: User): Boolean = userId.equals(user.userId)
    fun fillWith(user: User) = this.copy(
            displayName = user.displayName,
            imageUrl = user.imageUrl,
            email = user.email)
}


data class Comment(val commentId:String,
                   val userId: String,
                   val name: String,
                   val message: String)

data class Guest(val guestId: String,
                 val userId: String,
                 val displayName: String,
                 val imageUrl: String?,
                 val email: String)

/// Push new element response, name has new ID
data class PushResponse(val name:String)
data class GuestResponse(val eventId: String,
                         val userId: String)
data class PollResponse(val eventId: String,
                        val question: String,
                        val answers: Map<AnswerResponse,Map<UserIdResponse,Boolean>>) {
    fun hasUserAlreadyVoted(userId: UserIdResponse) : Boolean =
            answers.flatMap { (answer, votes) -> votes.keys }.contains(userId)
}
data class CommentResponse(val eventId:String,
                           val userId: String,
                           val name: String,
                           val message: String)
data class DeleteResponse(val nothing: String?)

data class TransportResponse(val eventId: String,
                             val driver: String,
                             val passengers: ArrayList<String>,
                             val locationName: String,
                             val latitude: String,
                             val longitude: String,
                             val totalSlots: String)

data class TaskResponse(val eventId: String,
                        val description: String,
                        val responsible: String)

data class ImageResponse(val error: Boolean,
                         val image: String?)

interface Mapper<E, D> {
    fun mapToDomain(id: String, entity: E): D
    fun mapToEntity(referenceId: String = "", domain: D): E
}

class UserMapper : Mapper<UserResponse, User> {

    override fun mapToDomain(userId: String, entity: UserResponse): User {

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

    override fun mapToEntity(referenceId: String, domain: User): UserResponse = mapToEntity(domain)
    fun mapToEntity(user: User): UserResponse = UserResponse(
            user.displayName,
            user.imageUrl,
            user.email)
}

class EventMapper : Mapper<EventResponse, Event> {
    override fun mapToDomain(eventId: String, entity: EventResponse): Event = Event(
            eventId,
            entity.title,
            entity.imageUrl,
            entity.description,
            DateTime.parse(entity.startDate),
            DateTime.parse(entity.endDate),
            Location(entity.location, Coordinates(entity.latitude.toDouble(), entity.longitude.toDouble())),
            entity.user)

    override fun mapToEntity(referenceId: String, domain: Event): EventResponse = mapToEntity(domain)

    fun mapToEntity(event: Event): EventResponse = EventResponse(
            event.title,
            event.imageUrl,
            event.description,
            event.startDate.toString(),
            event.endDate.toString(),
            event.location.name,
            event.location.coordinates.latitude.toString(),
            event.location.coordinates.longitude.toString(),
            event.user)

}

class PollMapper : Mapper<PollResponse, Poll> {
    override fun mapToDomain(pollId: String, entity: PollResponse): Poll =
        if(!entity.hasUserAlreadyVoted(SessionManager.getCurrentUser()!!.userId)) {
            val answers = mapAnswers(entity.answers, { answer, votes -> Answer.Open(answer, votes) })
            Poll.Votable(pollId=pollId, eventId=entity.eventId, question=entity.question, answers=answers)
        }
        else {
            val answers = mapAnswers(entity.answers, { answer, votes -> Answer.Closed(answer, votes) })
            Poll.NoVotable(pollId=pollId, eventId=entity.eventId, question=entity.question, answers=answers)
        }

    override fun mapToEntity(referenceId: String, domain: Poll): PollResponse = mapToEntity(domain)

    fun mapToEntity(poll: Poll): PollResponse = PollResponse(
            eventId=poll.eventId,
            question=poll.question,
            answers=poll.answers.map { Pair(it.text, it.votes.map { Pair(it, true) }.plus(Pair("placeholder", true)).toMap()) }.toMap())

    private fun<AnswerType> mapAnswers(entityAnswers: Map<AnswerResponse,Map<UserIdResponse,Boolean>>, constructor: (String, List<String>) -> AnswerType) =
            entityAnswers.map { (answer, votes) -> constructor(answer, votes.keys.toList().filter { userId -> userId != "placeholder" }) }
}

class CommentMapper : Mapper<CommentResponse, Comment> {

    override fun mapToDomain(commentId: String, entity: CommentResponse): Comment = Comment(
            commentId,
            entity.userId,
            entity.name,
            entity.message)

    override fun mapToEntity(eventId: String, comment: Comment): CommentResponse = CommentResponse(
            eventId,
            comment.userId,
            comment.name,
            comment .message)

}

class GuestMapper : Mapper<GuestResponse, Guest> {

    override fun mapToDomain(guestId: String, entity: GuestResponse): Guest = Guest(
            guestId,
            entity.userId,
            "",
            "",
            "")

    override fun mapToEntity(eventId: String, guest: Guest): GuestResponse = GuestResponse(eventId, guest.userId)

}

class TransportMapper : Mapper<TransportResponse, TransportItem> {

    override fun mapToDomain(transportId: String, entity: TransportResponse): TransportItem = TransportItem(
            transportId,
            User(entity.driver, "", "", ""),
            entity.passengers?.map { User(it, "", "", "") }?.toCollection(ArrayList()) ?: ArrayList(),
            Location(entity.locationName, Coordinates(entity.latitude.toDouble(), entity.longitude.toDouble())),
            entity.totalSlots.toInt()
    )

    override fun mapToEntity(eventId: String, transport: TransportItem): TransportResponse = TransportResponse(
            eventId,
            transport.driver.userId,
            transport.passangers.map { it.userId }.toCollection(ArrayList()),
            transport.startpoint.name,
            transport.startpoint.latLng().latitude.toString(),
            transport.startpoint.latLng().longitude.toString(),
            transport.totalSlots.toString()
    )

}


class TaskMapper : Mapper<TaskResponse, TaskItem> {

    override fun mapToDomain(taskId: String, entity: TaskResponse): TaskItem = TaskItem(
            taskId,
            entity.description,
            entity.responsible)

    override fun mapToEntity(eventId: String, task: TaskItem): TaskResponse = TaskResponse(
            eventId,
            task.description,
            task.responsible)

}