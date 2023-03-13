package com.lyescorp

import androidx.lifecycle.*
import com.google.gson.Gson
import com.lyescorp.models.*
import com.lyescorp.server.RetrofitConnection
import kotlinx.coroutines.launch


class MainViewModel: ViewModel() {
    // Generamos liveData que son objetos que desde fuera se pueden
    // suscribir de tal manera que cuando el valor en el viewModel se modifique
    // el subscritor Observable se enterá y refrescarà al UI !!

    // Si estamos refrescando datos
    // 1. Una parte privada para uso interno el MutableLiveData permite asignar valores
    private val _loading = MutableLiveData(false)
    // 2. una parte pública para que desde fuera se puedan suscribir, el liveData solo de lectura
    val loading: LiveData<Boolean> get() = _loading

    // El  número de películas, JSON y MovieDB
    private val _moviesCount = MutableLiveData(0)
    val moviesCount: LiveData<Int> get() = _moviesCount
    private val _moviesdbCount = MutableLiveData(0)
    val moviesdbCount: LiveData<Int> get() = _moviesdbCount

    // El List de películas que devuelve cada una de las ApiRest
    private val _moviesdb = MutableLiveData<List<MovieResponse>>(emptyList())
    val moviesdb: LiveData<List<MovieResponse>> get() = _moviesdb
    private val _movies = MutableLiveData<List<Movie>>(emptyList())
    val movies: LiveData<List<Movie>> get() = _movies
    //Objeto weather
    private val _weatherdata = MutableLiveData<WeatherClass>()
    val weatherdata: LiveData<WeatherClass> get() = _weatherdata

    // Error en la comunicación con la ApiRest
    private val _errorApiRest = MutableLiveData<String?>(null)
    val errorApiRest: LiveData<String?> get() = _errorApiRest

    // Creamos la instancia de OpenAI, solo para JSON-Server

    init {
        loadMovies()
        getWeatherResp()
    }

    fun getWeatherResp(){
        //GET WeatherResponse
        viewModelScope.launch {
            _loading.value = true
            _errorApiRest.value = null
            var response = RetrofitConnection.service.getLocalConf()

            if(response.isSuccessful){
                var gson = Gson();
                var jsonRes = gson.toJson(response.body())
                var c = gson.fromJson(jsonRes, Array<Conf>::class.java)
                var resp2 = RetrofitConnection.service3.getWeatherResponse(c[0].location!!)
                if(resp2.isSuccessful){
                   var converted = gson.fromJson(gson.toJson(resp2.body()), WeatherClass::class.java)
                   _weatherdata.value = converted


                }else{
                    _errorApiRest.value = resp2.errorBody().toString()
                }
            }else{
                _errorApiRest.value = response.errorBody().toString()
            }
        }


    }
     fun loadMovies() {
        // esto se ejecuta en el mismo instante que el viewModel se pone en marcha
        // sería como el onCreate de una activity.

        // cargamos las películas
        viewModelScope.launch {
            _loading.value = true
            _errorApiRest.value = null

            var response = RetrofitConnection.service.listMovies()

            if (response.isSuccessful) {

                _movies.value = response.body()

                _moviesCount.value = _movies.value!!.size
            }
            else {
                _errorApiRest.value = response.errorBody().toString()
            }

            _loading.value = false

        }
    }

     fun loadMoviesDB(keyword:String) {

        viewModelScope.launch {
            _loading.value = true
            _errorApiRest.value = null
            //Declaramos las variables donde vamos a enchufar las pelis y el response de la API
            var moviedblist = arrayListOf<MovieResponse>()
            var response = RetrofitConnection.service2.getListMoviesDB(keyword)
            //Si te obtiene datos...Entra
            if (response.isSuccessful) {
                //WARNING, Código que no se entiende
                val gson = Gson(); //Declaro el GSON para el tratado de JSON
                var jsonRes = gson.toJson(response.body()) //Obtengo el objeto general en forma de JSON
                var mvdbObject = gson.fromJson(jsonRes, MovieDBDataResponse::class.java) // Lo paso a objeto general con mi data class
                //For que recorre el array que contiene las fcking movies i las añade a las peliculas
                for (i in 0 until mvdbObject.results.size) {
                    val item = mvdbObject.results[i]
                    val itjson = gson.toJson(item)
                    var conv = gson.fromJson(itjson, MovieResponse::class.java)
                    moviedblist.add(conv)
                }
                _moviesdb.value = moviedblist.sortedBy { it.title }
                _moviesdbCount.value = _moviesdb.value!!.size


            }
            else {
                _errorApiRest.value = response.errorBody().toString()
            }

            _loading.value = false

        }

    }
    fun newMovie(movie:Movie){
        viewModelScope.launch {
            newMovie(movie)
        }
    }

    fun onMovieClicked(movie: Movie) {

        viewModelScope.launch {
            // actualizamos
            RetrofitConnection.service.updateMovie(movie.id!!, movie)
            // recargamos la lista que el observable de la activity recargará.
            loadMovies()
        }
    }

    fun onMovieDelete(movie: Movie) {
        viewModelScope.launch {
            // actualizamos
            RetrofitConnection.service.deleteMovie(movie.id!!)
            // recargamos la lista que el observable de la activity recargará.
            loadMovies()
        }
    }

    fun updateConfig(poblacion:String){
        viewModelScope.launch {
            var newConf = Conf(1,poblacion)
            RetrofitConnection.service.updateConf(newConf)
        }
    }





}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel() as T
    }
}

