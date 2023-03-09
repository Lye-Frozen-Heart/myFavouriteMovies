package com.mp08.myfavouritemovies.server

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitWeatherEndpoints {
    //Endpoints de The Weather API
    @GET("http://api.weatherapi.com/v1/current.json?key=681960b59bd44c9abb9151241231002&q={poblacio}&aqi=no")
    suspend fun getWeatherResponse(@Path("poblacio") poblacio :String ): Response<Any>

}