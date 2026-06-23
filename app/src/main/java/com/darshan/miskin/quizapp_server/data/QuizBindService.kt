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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@AndroidEntryPoint
class QuizBindService : LifecycleService() {
    var quizRepository: QuizRepository? = null
    private val coroutineSessionMap = ConcurrentHashMap<Int, Job>()
    private val sessionMap = ConcurrentHashMap<Int, SessionData>()
    private val callbackList = object : RemoteCallbackList<IQuizCallBackInterface>() {
        override fun onCallbackDied(
            callbackInterface: IQuizCallBackInterface?,
            cookie: Any?
        ) {
            super.onCallbackDied(callbackInterface, cookie)
            if (cookie is Int) {
                sessionMap.remove(cookie)
                coroutineSessionMap.remove(cookie)
            }
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

    fun sendSafeBroadCast(uid: Int, callback: (IQuizCallBackInterface) -> Unit) {
        try {
            val count = callbackList.beginBroadcast()
            for (i in 0 until count) {
                if (uid == callbackList.getBroadcastCookie(i)) {
                    callback.invoke(callbackList.getBroadcastItem(i))
                }
            }
        }
        finally {
            callbackList.finishBroadcast()
        }
    }

    val iQuizDataInterface = object : IQuizDataInterface.Stub() {
        override fun getNextQuestion(): QuizData? {
            val clientData = sessionMap[getCallingUid()]
            var isQuizComplete = false
            var nextQuestion : QuizData? = null

            if (clientData==null)
                return null

            synchronized(clientData){
                if (clientData.questionCounter == 10)
                    isQuizComplete = true
                else
                    nextQuestion = clientData.list[clientData.questionCounter++]
            }

            if (isQuizComplete)
                sendSafeBroadCast(getCallingUid()) { it.onQuizComplete() }

            return nextQuestion
        }

        override fun startQuiz() {
            val uid = getCallingUid()
            coroutineSessionMap[uid]?.cancel()
            sessionMap[uid] = SessionData().apply {
                questionCounter = 0
                list = emptyList()
            }
            coroutineSessionMap[uid] = lifecycleScope.launch(Dispatchers.IO) {
                quizRepository?.getQuizData()?.collect {
                    when (it) {
                        is ResponseState.Error -> {
                            sendSafeBroadCast(uid){ callbackInterface -> callbackInterface.onError(it.errorMessage) }
                        }
                        is ResponseState.Success<List<QuizData>> -> {
                            sessionMap[uid]?.list = it.data
                            sendSafeBroadCast(uid) { callBackInterface -> callBackInterface.onQuizLoaded()}
                        }
                    }
                }
            }
        }

        override fun registerQuizCallback(iQuizCompleteInterface: IQuizCallBackInterface?) {
            callbackList.register(iQuizCompleteInterface, getCallingUid())
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