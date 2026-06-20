package com.darshan.miskin.quizapp_server.data

import com.darshan.miskin.quizapp_server.data.state.ResponseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class QuizRepository @Inject constructor(val quizApiService: QuizApiService) {

    fun getQuizData() = flow {
        try {
            val response = quizApiService.getQuizData()
            if(response.response_code == 0){
                emit(ResponseState.Success(response.results))
            }
            else {
                emit(ResponseState.Error("(Error) API Response Code: ${response.response_code}"))
            }
        }
        catch (e: Exception){
            emit(ResponseState.Error(e.message))
        }
    }.flowOn(Dispatchers.IO)
}