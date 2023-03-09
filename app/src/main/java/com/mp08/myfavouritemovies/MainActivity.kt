package com.mp08.myfavouritemovies

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.lyescorp.myfavouritemovies.R
import com.lyescorp.myfavouritemovies.databinding.ActivityMainBinding
import com.mp08.myfavouritemovies.server.RetrofitConnection.service
import com.mp08.myfavouritemovies.viewmodels.MainViewModel
import com.mp08.myfavouritemovies.viewmodels.MainViewModelFactory


class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels { MainViewModelFactory() }

    private lateinit var binding: ActivityMainBinding

    // Creamos el adapter del recyclerView de Movies Normal, el que va al JSOn
    private val adapter = MoviesAdapter(emptyList(), this,
                                    {viewModel.onMovieClicked(it) },
                                    {viewModel.onMovieDelete(it)})


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Para trabajar con ViewBinding, igualamos el binding al Binding correspondiente de la activity, cada una tiene uno correspondiente
        //por defecto, seteando la view que toca
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Configuramos el recyclerView
        //Seteamos el adaptador de la MainActivity al adaptador que queramos utilizar. Recuerda que sin esto no podras notificar los cambios
        //Ni recuperar la lista del adaptador!
        binding.rvMovies.adapter = adapter



        // Nos suscribimos al loading de viewModel para que cuando esté cargando la lista se utilize el gif de progreso
        viewModel.loading.observe(this) { cargando ->
            if (cargando) {
                binding.progress.visibility = View.VISIBLE
            }
            else {
                binding.progress.visibility = View.GONE
            }
        }

        // Nos suscribimos al moviesCount, esto cuenta cuantas Movies hay en el JSON
        viewModel.moviesCount.observe(this) { count ->
            binding.tvMoviesCount.text = count.toString()
        }

        // Nos suscribimos a movies, para observar si la lista ha cambiado. Muy eficiente y potente!
        viewModel.movies.observe(this) {
            adapter.movies = it
            adapter.notifyDataSetChanged()
        }

        // Nos suscribimos a cuando llega un error de la api. Esto peta el programa si no hay ninguna Movie
        viewModel.errorApiRest.observe(this) {
            if (it != null) {
                Snackbar.make(findViewById(android.R.id.content),
                    it,
                    Snackbar.LENGTH_LONG).show()
            }
        }
    }

    // creamos el menú.
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    // gestionamos el menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) { //Refrescar manualmente las peliculas
            R.id.mnuRefresh -> {
                viewModel.loadMovies()
            }//Añadir nueva peli, llamamos a un Dialogo con un Input de texto y le pasamos una Keyword a buscar en la API
            R.id.mnuAdd -> {
                var input = EditText(this)
                input.inputType = InputType.TYPE_CLASS_TEXT
                val builder = MaterialAlertDialogBuilder(this)
                var m_Text:String
                builder.setTitle(R.string.introfilm)
                    .setView(input)

                builder.setPositiveButton("Buscar")
                { _, _ ->
                  m_Text = input.text.toString()
                  openSomeActivityForResult(m_Text)


                }
                builder.setNegativeButton("Cancelar")
                { dialog, _ -> dialog.cancel() }
                builder.show()
            }
            R.id.weather ->{

            }
            else -> super.onOptionsItemSelected(item)
        }

        return(super.onOptionsItemSelected(item));
    }

    fun openSomeActivityForResult(keyword:String) {
        val intent = Intent(this, MovieSelectorActivity::class.java)
        intent.putExtra("keyword",keyword)
        resultLauncher.launch(intent)
    }

    var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            adapter.notifyDataSetChanged()
            viewModel.loadMovies()
        }
    }

}