// QuizData.aidl
package com.darshan.miskin.quizapp_server;

parcelable QuizData{
    String category;
    String correct_answer;
    String difficulty;
    List<String> incorrect_answers;
    String question;
    String type;
}