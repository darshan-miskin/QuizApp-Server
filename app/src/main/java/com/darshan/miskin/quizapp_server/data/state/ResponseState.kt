package com.darshan.miskin.quizapp_server.data.state

sealed interface ResponseState<out T> {
    object Loading: ResponseState<Nothing>
    class Error(val errorMessage: String?): ResponseState<Nothing>
    class Success<out T>(val data: T): ResponseState<T>
}