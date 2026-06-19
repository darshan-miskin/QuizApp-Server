package com.darshan.miskin.quizapp_server.data.model

import com.darshan.miskin.quizapp_server.QuizData

data class QuizApiResponse(
    val response_code: Int,
    val results: List<QuizData>
)