package com.cronberry.fcmpushnotification

import android.app.Activity
import com.cronberry.cronberryfcmdemo.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Utility {

    companion object {
        private var retrofitObj: Retrofit? = null

        fun getRetrofitObj(act: Activity): WebAPI? {

            val BITBUDDY_TIMEOUT: Long = 1
            val client = OkHttpClient().newBuilder()
                .connectTimeout(BITBUDDY_TIMEOUT, TimeUnit.MINUTES)
                .readTimeout(BITBUDDY_TIMEOUT, TimeUnit.MINUTES)
                .writeTimeout(BITBUDDY_TIMEOUT, TimeUnit.MINUTES)
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level =
                        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                })
                .addInterceptor { chain ->
                    val original = chain.request()
                    val request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .method(original.method, original.body)
                        .build()

                    val proceed = chain.proceed(request)
                    proceed

                }
                .build()
            retrofitObj = Retrofit.Builder()
                .baseUrl("https://api.cronberry.com/cronberry/api/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofitObj?.create(WebAPI::class.java)
        }
    }
}