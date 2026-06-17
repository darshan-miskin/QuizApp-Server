package com.darshan.miskin.quizapp_server.data.model

data class QuizApiResponse(
    val response_code: Int,
    val quizDataList: List<QuizData>
)