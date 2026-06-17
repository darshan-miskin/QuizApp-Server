package com.darshan.miskin.quizapp_server.di

import com.darshan.miskin.quizapp_server.data.QuizApiService
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://opentdb.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideQuizApiService(retrofit: Retrofit): QuizApiService{
        return retrofit.create(QuizApiService::class.java)
    }
}