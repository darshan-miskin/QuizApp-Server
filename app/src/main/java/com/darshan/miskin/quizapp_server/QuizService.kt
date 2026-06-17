package com.darshan.miskin.quizapp_server

import android.app.Service
import android.content.Intent
import android.os.IBinder

class QuizService : Service() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}