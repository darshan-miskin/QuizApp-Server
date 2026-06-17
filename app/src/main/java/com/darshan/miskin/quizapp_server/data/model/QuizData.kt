package com.darshan.miskin.quizapp_server.data.model

data class QuizData(
    val category: String,
    val correct_answer: String,
    val difficulty: String,
    val incorrect_answers: List<String>,
    val question: String,
    val type: String
)