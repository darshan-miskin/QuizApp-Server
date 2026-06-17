package com.darshan.miskin.quizapp_server.data

import com.darshan.miskin.quizapp_server.data.state.ResponseState
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class QuizRepository @Inject constructor(val quizApiService: QuizApiService) {

    fun getQuizData() = flow {
        emit(ResponseState.Loading)
        try {
            val response = quizApiService.getQuizData()
            if(response.response_code == 0){
                emit(ResponseState.Success(response.quizDataList))
            }
            else {
                emit(ResponseState.Error("(Error) API Response Code: ${response.response_code}"))
            }
        }
        catch (e: Exception){
            emit(ResponseState.Error(e.message))
        }
    }
}