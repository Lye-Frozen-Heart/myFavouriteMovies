package com.lyescorp.server

import retrofit2.Response
import retrofit2.http.*


interface RetrofitEndPointsMovieDB {
    //Endpoints de TheMovieDatabase
    @GET("search/movie?api_key=3fca9bc484d9be5e4a6a8ace1a89e8b8")
    suspend fun getListMoviesDB(@Query ("query") keyword : String): Response<Any>
}