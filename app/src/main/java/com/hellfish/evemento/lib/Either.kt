package com.hellfish.evemento.lib

sealed class Either<out A, Error> {
    abstract fun<B> map(f: (A) -> B): Either<B, Error>
    abstract fun<B> flatMap(f: (A) -> Either<B, Error>): Either<B, Error>
    abstract fun onSuccess(f: (A) -> Unit): Either<A, Error>
    abstract fun onError(f: (Error) -> Unit): Either<A, Error>

    data class Error<out A, Error>(val error: Error) : Either<A, Error>() {
        override fun <B> flatMap(f: (A) -> Either<B, Error>): Either<B, Error> = Error(error)

        override fun onSuccess(f: (A) -> Unit) : Either<A, Error> = this

        override fun onError(f: (Error) -> Unit) : Either<A, Error> {
            f(error)
            return this
        }

            override fun<B> map(f: (A) -> (B)) : Either<B, Error> = Error(error)
    }

    data class Success<out A, Error>(val value: A) : Either<A, Error>() {
        override fun <B> flatMap(f: (A) -> Either<B, Error>) : Either<B, Error> = f(value)

        override fun onSuccess(f: (A) -> Unit) : Either<A, Error> {
            f(value)
            return this
        }

        override fun onError(f: (Error) -> Unit) : Either<A, Error> = this

        override fun<B> map(f: (A) -> B) : Either<B, Error> = Success(f(value))
    }
}