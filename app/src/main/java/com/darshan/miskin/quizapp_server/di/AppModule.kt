package com.darshan.miskin.quizapp_server.di

import com.darshan.miskin.quizapp_server.BuildConfig
import com.darshan.miskin.quizapp_server.data.QuizApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@InstallIn(SingletonComponent::class)
@Module
class AppModule {
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideQuizApiService(retrofit: Retrofit): QuizApiService{
        return retrofit.create(QuizApiService::class.java)
    }
}