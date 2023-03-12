package com.lyescorp


import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.lyescorp.myfavouritemovies.databinding.ActivityMoviesSelectorBinding

@Suppress("DEPRECATION")
class MovieSelectorActivity:AppCompatActivity() {
    private lateinit var binding: ActivityMoviesSelectorBinding
    //Importante, el ViewModel es el Middleware que tiene toda la lógica de consumo de API's, sientete libre de montar muchas
    //cosas dentro del mismo ViewModel
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory() }
    private var adapter = MoviesDBAdapter(emptyList(),this)
        override fun onCreate(savedInstanceState: Bundle?){
            super.onCreate(savedInstanceState)
            supportActionBar?.setDisplayHomeAsUpEnabled(true);
            supportActionBar?.setDisplayShowHomeEnabled(true);
            //Lo mismo que en MainActivity igualamos el binding al que pertoca
            binding = ActivityMoviesSelectorBinding.inflate(layoutInflater)
            val view = binding.root
            setContentView(view)
            //Seteamos el adaptador al RecyclerView para poder jugar con él
            binding.rvMoviesDB.adapter = adapter
            //Observador para la lista de Peliculas de MovieDB
            viewModel.moviesdb.observe(this){
                adapter.movies = it
                adapter.notifyDataSetChanged()
            }

            //Conteo de pelis...
            viewModel.moviesdbCount.observe(this) { count ->
                binding.txtvwSel2.text = count.toString()
            }
            //Recibir la keyword y cargar la lista al Iniciar! Consumo de API!!
            val kword:String = intent.getSerializableExtra("keyword") as String
            viewModel.loadMoviesDB(kword)
            adapter.movies = viewModel.moviesdb.value!!


        }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }


}