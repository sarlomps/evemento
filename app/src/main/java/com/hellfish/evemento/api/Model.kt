package com.hellfish.evemento.api

import com.google.firebase.auth.FirebaseUser
import com.hellfish.evemento.SessionManager
import com.hellfish.evemento.event.Event
import com.hellfish.evemento.event.poll.Answer
import com.hellfish.evemento.event.poll.Poll
import org.joda.time.DateTime

typealias UserIdResponse = String
typealias AnswerResponse = String

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
            entity.location,
            entity.user)

    override fun mapToEntity(referenceId: String, domain: Event): EventResponse = mapToEntity(domain)

    fun mapToEntity(event: Event): EventResponse = EventResponse(
            event.title,
            event.imageUrl,
            event.description,
            event.startDate.toString(),
            event.endDate.toString(),
            event.location,
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