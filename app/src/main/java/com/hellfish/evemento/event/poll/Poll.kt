package com.hellfish.evemento.event.poll

import com.google.firebase.auth.FirebaseUser

typealias EvementoUser = FirebaseUser

sealed class Poll(open val question: String, open val answers: List<Answer>) {
    fun totalVotes() : Int = answers.sumBy { it.votesAmount }

    companion object {
        fun forUser(user: EvementoUser, question: String, answers: List<Answer>) : Poll  =
            if(answers.any { it.wasVotedBy(user) }) {
                NoVotable(question, answers as List<Answer.Closed>)
            } else Votable(question, answers as List<Answer.Open>)
    }

    class Votable(override val question: String,
                  override val answers: List<Answer.Open>) : Poll(question, answers) {
        fun choose(answer: Answer.Open, user: EvementoUser) : NoVotable {
            val updatedAnswers = answers.map { anAnswer -> if (answer == anAnswer) { anAnswer.vote(user) } else { anAnswer.close() } }
            return NoVotable(question, updatedAnswers)
        }
    }

    class NoVotable(override val question: String,
                    override val answers: List<Answer.Closed>) : Poll(question, answers)
}