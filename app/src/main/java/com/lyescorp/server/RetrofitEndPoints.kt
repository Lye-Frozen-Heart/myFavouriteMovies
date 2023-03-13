package com.lyescorp.server

import com.lyescorp.models.Conf
import com.lyescorp.models.Movie
import retrofit2.Response
import retrofit2.http.*


interface RetrofitEndPoints {
    //Endpoints del JSON-Server
    @GET("movies")
    suspend fun listMovies(): Response<List<Movie>>

    @GET("movies/{id}")
    suspend fun getMovieBool(@Path("id") id: Long): Response<Movie>

    @GET("conf")
    suspend fun getLocalConf():Response<Any>

    @PUT("conf/1")
    suspend fun updateConf(@Body location: Conf )


    @POST("movies")
    suspend fun newMovie(@Body movie: Movie?)

    @PUT("movies/{id}")
    suspend fun updateMovie(@Path("id") id: Long, @Body movie: Movie?)

    @DELETE("movies/{id}")
    suspend fun deleteMovie(@Path("id") id: Long)

}