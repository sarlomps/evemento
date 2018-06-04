package com.hellfish.evemento.event.poll

sealed class Answer(val text: String, val votes: Int) {
    fun percentageFrom(totalVotes: Int) = votes.toFloat() / totalVotes.toFloat()

    class Open(text: String, votes: Int) : Answer(text, votes) {
        fun vote() : Closed = Closed(text, votes + 1)

        fun close() : Closed = Closed(text, votes)
    }
    class Closed(text: String, votes: Int) : Answer(text, votes)
}