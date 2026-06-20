package com.darshan.miskin.quizapp_server.data.model

import com.darshan.miskin.quizapp_server.QuizData

data class SessionData(
    var list: List<QuizData> = emptyList(),
    var questionCounter: Int = 0
)
