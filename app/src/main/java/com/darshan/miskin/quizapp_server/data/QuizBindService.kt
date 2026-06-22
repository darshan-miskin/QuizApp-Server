package com.darshan.miskin.quizapp_server.data

import android.content.Intent
import android.os.IBinder
import android.os.RemoteCallbackList
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.darshan.miskin.quizapp_server.IQuizCallBackInterface
import com.darshan.miskin.quizapp_server.IQuizDataInterface
import com.darshan.miskin.quizapp_server.QuizData
import com.darshan.miskin.quizapp_server.contract.QuizContract
import com.darshan.miskin.quizapp_server.data.model.SessionData
import com.darshan.miskin.quizapp_server.data.state.ResponseState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@AndroidEntryPoint
class QuizBindService : LifecycleService() {
    var quizRepository: QuizRepository? = null
    private val sessionMap = ConcurrentHashMap<Int, SessionData>()
    private val callbackList = object : RemoteCallbackList<IQuizCallBackInterface>(){
        override fun onCallbackDied(
            callbackInterface: IQuizCallBackInterface?,
            cookie: Any?
        ) {
            super.onCallbackDied(callbackInterface, cookie)
            if (cookie is Int)
                sessionMap.remove(cookie)
        }
    }

    @Inject
    fun initQuizRepository(quizRepository: QuizRepository) {
        this.quizRepository = quizRepository
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        if (intent.action != QuizContract.ACTION_START_QUIZ)
            return null
        return iQuizDataInterface
    }

    val iQuizDataInterface = object : IQuizDataInterface.Stub() {
        override fun getNextQuestion(): QuizData? {
            val clientData = sessionMap.getValue(getCallingUid())
            if (clientData.questionCounter == 10) {
                val count = callbackList.beginBroadcast()
                for (i in 0 until count) {
                    callbackList.getBroadcastItem(i).onQuizComplete()
                }
                callbackList.finishBroadcast()
                return null
            }
            return clientData.list[clientData.questionCounter++]
        }

        override fun startQuiz() {
            val uid = getCallingUid()
            sessionMap[uid] = SessionData().apply {
                questionCounter = 0
                list = emptyList()
            }
            lifecycleScope.launch(Dispatchers.IO) {
                quizRepository?.getQuizData()?.collect {
                    when (it) {
                        is ResponseState.Error -> {
                            val count = callbackList.beginBroadcast()
                            for (i in 0 until count) {
                                if (uid == callbackList.getBroadcastCookie(i))
                                    withContext(Dispatchers.Main) {
                                        callbackList.getBroadcastItem(i).onError(it.errorMessage)
                                    }
                                callbackList.finishBroadcast()
                            }
                        }

                        is ResponseState.Success<List<QuizData>> -> {
                            sessionMap.getValue(uid).list = it.data
                            val count = callbackList.beginBroadcast()
                            for (i in 0 until count) {
                                if (uid == callbackList.getBroadcastCookie(i))
                                    withContext(Dispatchers.Main) {
                                        callbackList.getBroadcastItem(i).onQuizLoaded()
                                    }
                                callbackList.finishBroadcast()
                            }
                        }
                    }
                }
            }
        }

        override fun registerQuizCallback(iQuizCompleteInterface: IQuizCallBackInterface?) {
            callbackList.register(iQuizCompleteInterface, getCallingUid())
            sessionMap[getCallingUid()] = SessionData()
        }

        override fun unregisterQuizCallback(iQuizCompleteInterface: IQuizCallBackInterface?) {
            callbackList.unregister(iQuizCompleteInterface)
        }
    }

    override fun onDestroy() {
        sessionMap.clear()
        callbackList.kill()
        quizRepository = null
        super.onDestroy()
    }
}