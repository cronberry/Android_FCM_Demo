package com.cronberry.fcmpushnotification

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface WebAPI {

    @POST("campaign/register-audience-data")
    fun registerAudience(@Body map: HashMap<String, Any>): Call<LinkedHashMap<Any, Any>>
}