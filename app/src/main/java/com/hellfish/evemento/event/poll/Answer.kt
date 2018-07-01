package com.hellfish.evemento.event.poll

sealed class Answer(val text: String, val votes: List<EvementoUser>) {
    fun percentageFrom(totalVotes: Int) = votesAmount.toFloat() / totalVotes.toFloat()

    val votesAmount = votes.size

    fun wasVotedBy(user: EvementoUser) = votes.contains(user)

    class Open(text: String, votes: List<EvementoUser>) : Answer(text, votes) {
        fun vote(user : EvementoUser) : Closed = Closed(text, votes.plus(user))

        fun close() : Closed = Closed(text, votes)
    }
    class Closed(text: String, votes: List<EvementoUser>) : Answer(text, votes)
}