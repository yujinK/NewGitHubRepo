package com.yujin.newgithubrepo

import android.app.Application
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class NewGitHubReposApplication : Application() {
    private lateinit var retrofit: Retrofit
    private lateinit var gitHubService: GitHubService

    override fun onCreate() {
        super.onCreate()
        setupAPIClient()
    }

    private fun setupAPIClient() {
        val logging = HttpLoggingInterceptor { message ->
            Log.d("API LOG", message)
        }

        logging.level = HttpLoggingInterceptor.Level.BASIC

        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(logging).build()

        retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .baseUrl("https://api.github.com")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        gitHubService = retrofit.create(GitHubService::class.java)
    }

    fun getGitHubService(): GitHubService {
        return gitHubService
    }
}