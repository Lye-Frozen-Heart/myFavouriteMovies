package com.lyescorp.server

import com.lyescorp.models.Movie
import retrofit2.Response
import retrofit2.http.*


interface RetrofitEndPoints {
    //Endpoints del JSON-Server
    @GET("movies")
    suspend fun listMovies(): Response<List<Movie>>

    @GET("movies/{id}")
    suspend fun getMovieBool(@Path("id") id: Long):Movie

    @GET("conf")
    suspend fun getLocalConf():Response<Any>

    @PUT("conf/{id}/{location}")
    suspend fun updateConf(@Path("id") id: Int, @Query("location") location: String)


    @POST("movies")
    suspend fun newMovie(@Body movie: Movie?)

    @PUT("movies/{id}")
    suspend fun updateMovie(@Path("id") id: Long, @Body movie: Movie?)

    @DELETE("movies/{id}")
    suspend fun deleteMovie(@Path("id") id: Long)

}