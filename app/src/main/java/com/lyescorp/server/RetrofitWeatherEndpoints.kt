package com.lyescorp.server

import retrofit2.Response
import retrofit2.http.*

interface RetrofitWeatherEndpoints {
    //Endpoints de The Weather API
    @GET("current.json?key=681960b59bd44c9abb9151241231002&aqi=no")
    suspend fun getWeatherResponse(@Query("q")poblacio :String ): Response<Any>

}