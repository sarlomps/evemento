package com.hellfish.evemento.event.poll

typealias EvementoUser = String

sealed class Poll(open val pollId: String, open val eventId: String, open val question: String, open val answers: List<Answer>) {

    fun totalVotes() : Int = answers.sumBy { it.votesAmount }

    class Votable(override val eventId: String,
                  override val pollId: String,
                  override val question: String,
                  override val answers: List<Answer.Open>) : Poll(eventId, pollId, question, answers) {
        fun choose(answer: Answer.Open, user: EvementoUser) : NoVotable {
            val updatedAnswers = answers.map { anAnswer -> if (answer == anAnswer) { anAnswer.vote(user) } else { anAnswer.close() } }
            return NoVotable(eventId, pollId, question, updatedAnswers)
        }
    }

    class NoVotable(override val eventId: String,
                    override val pollId: String,
                    override val question: String,
                    override val answers: List<Answer.Closed>) : Poll(eventId, pollId, question, answers)
}