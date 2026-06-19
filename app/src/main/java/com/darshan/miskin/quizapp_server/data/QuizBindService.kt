package com.darshan.miskin.quizapp_server.data

import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.darshan.miskin.quizapp_server.IQuizCallBackInterface
import com.darshan.miskin.quizapp_server.IQuizDataInterface
import com.darshan.miskin.quizapp_server.QuizData
import com.darshan.miskin.quizapp_server.data.state.ResponseState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class QuizBindService : LifecycleService() {
    @Inject
    lateinit var quizRepository: QuizRepository
    lateinit var list: List<QuizData>
    var questionCounter = 0
    var iQuizCallBackInterface: IQuizCallBackInterface? = null

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        if (intent.action == "com.darshan.miskin.ACTION_START_QUIZ") {
            lifecycleScope.launch(Dispatchers.IO) {
                quizRepository.getQuizData().collect {
                    when(it){
                        is ResponseState.Error -> {}
                        ResponseState.Loading -> {}
                        is ResponseState.Success<List<QuizData>> -> {
                            withContext(Dispatchers.Main){
                                Log.d("asdf", "Api Success!!")
                                list = it.data
                                iQuizCallBackInterface?.onQuizLoaded()
                            }
                        }
                    }
                }
            }
        }
        return iQuizDataInterface
    }

    val iQuizDataInterface = object : IQuizDataInterface.Stub() {
        override fun getNextQuestion(): QuizData? {
            if (questionCounter==10) {
                iQuizCallBackInterface?.onQuizComplete(true)
                return null
            }
            return list[questionCounter++]
        }

        override fun registerQuizCallback(iQuizCompleteInterface: IQuizCallBackInterface?) {
            this@QuizBindService.iQuizCallBackInterface = iQuizCompleteInterface
        }

        override fun unregisterQuizCallback(iQuizCompleteInterface: IQuizCallBackInterface?) {
            this@QuizBindService.iQuizCallBackInterface = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}