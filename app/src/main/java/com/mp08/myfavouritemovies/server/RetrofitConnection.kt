package com.mp08.myfavouritemovies.server

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create


object RetrofitConnection {

    //Cliente Http, interceptor para la WWW. Te permite construir las connexiones via internet
    private val okHttpClient = HttpLoggingInterceptor().run {
        level = HttpLoggingInterceptor.Level.BODY
        OkHttpClient.Builder()
            .addInterceptor(this).build()
    }
    //Builder de la librería retrofit, con su instacia puedes montar connexiones a cualquier servicio. En este caso el json-server
    private val builder = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    //Connexión a TheMovieDatabase. El 3 al final es la versión de la API. La más antigua pero la más completa...
    private val builder2 = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val builder3 = Retrofit.Builder()
            //TODO("PONER LA API DE WEATHER")
        .baseUrl("https://api.themoviedb.org/3/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //Services de Retrofit, para poder utilizar los builders como tal cada uno con sus endpoints como interfaz a consultar
    val service: RetrofitEndPoints = builder.create()
    val service2: RetrofitEndPointsMovieDB = builder2.create()
    val service3: RetrofitWeatherEndpoints = builder3.create()


}



