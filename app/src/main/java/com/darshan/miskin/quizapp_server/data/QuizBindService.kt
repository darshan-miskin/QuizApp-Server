package com.darshan.miskin.quizapp_server.data

import android.content.Intent
import android.os.IBinder
import android.os.RemoteCallbackList
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
    private val callbackList = RemoteCallbackList<IQuizCallBackInterface>()

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
                                val count = callbackList.beginBroadcast()
                                for (i in 0 until count){
                                    callbackList.getBroadcastItem(i).onQuizLoaded()
                                }
                                callbackList.finishBroadcast()
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
                val count = callbackList.beginBroadcast()
                for (i in 0 until count){
                    callbackList.getBroadcastItem(i).onQuizComplete(true)
                }
                callbackList.finishBroadcast()
                return null
            }
            return list[questionCounter++]
        }

        override fun registerQuizCallback(iQuizCompleteInterface: IQuizCallBackInterface?) {
            callbackList.register(iQuizCompleteInterface)
        }

        override fun unregisterQuizCallback(iQuizCompleteInterface: IQuizCallBackInterface?) {
            callbackList.unregister(iQuizCompleteInterface)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}