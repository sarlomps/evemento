package com.hellfish.evemento.event.poll

sealed class Poll(open val question: String, open val answers: List<Answer>) {

    fun totalVotes() : Int = answers.sumBy { it.votes }

    class Votable(override val question: String,
                  override val answers: List<Answer.Open>) : Poll(question, answers) {
        fun choose(answer: Answer.Open) : NoVotable {
            val updatedAnswers = answers.map { anAnswer -> if (answer == anAnswer) { anAnswer.vote() } else { anAnswer.close() } }
            return NoVotable(question, updatedAnswers)
        }
    }

    class NoVotable(override val question: String,
                    override val answers: List<Answer.Closed>) : Poll(question, answers)
}