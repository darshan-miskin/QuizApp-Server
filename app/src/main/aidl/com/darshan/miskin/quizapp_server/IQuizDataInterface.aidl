// IQuizDataInterface.aidl
package com.darshan.miskin.quizapp_server;

import com.darshan.miskin.quizapp_server.QuizData;
import com.darshan.miskin.quizapp_server.IQuizCompleteInterface;

interface IQuizDataInterface {
    QuizData getNextQuestion();
    boolean checkAnswer(int questionNumber, String answer);

    void registerQuizCallback(IQuizCompleteInterface iQuizCompleteInterface);
    void unregisterQuizCallback(IQuizCompleteInterface iQuizCompleteInterface);
}