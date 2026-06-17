package com.darshan.miskin.quizapp_server.data

import com.darshan.miskin.quizapp_server.data.model.QuizApiResponse
import retrofit2.http.GET

interface QuizApiService {
    @GET("api.php?amount=10&category=9")
    suspend fun getQuizData(): QuizApiResponse
}