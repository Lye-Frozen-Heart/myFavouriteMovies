package com.lyescorp

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.lyescorp.myfavouritemovies.R
import com.lyescorp.myfavouritemovies.databinding.ActivityMainBinding


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
        supportActionBar?.setTitle(R.string.titlemain)
        // Configuramos el recyclerView
        //Seteamos el adaptador de la MainActivity al adaptador que queramos utilizar. Recuerda que sin esto no podras notificar los cambios
        //Ni recuperar la lista del adaptador!
        binding.rvMovies.adapter = adapter
        // Nos suscribimos al loading de viewModel para que cuando esté cargando la lista se utilize el gif de progreso
        viewModel.loading.observe(this) { loading ->
            if (loading) {
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
    // Creamos el menú.
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        val filter = menu?.findItem(R.id.filter)
        filter?.setOnMenuItemClickListener {
            if(it.isChecked){
                adapter.movies = adapter.movies.sortedByDescending { it.title }
                adapter.notifyDataSetChanged();
                it.icon = ContextCompat.getDrawable(this, R.drawable.baseline_arrow_downward_24);
            }
            else{
                adapter.movies = adapter.movies.sortedBy { it.title }
                adapter.notifyDataSetChanged();
                it.icon = ContextCompat.getDrawable(this, R.drawable.baseline_arrow_upward_24);
            }
            it.isChecked = !it.isChecked
            true
        }
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
                  openSelectorActivityForResult(m_Text)
                }
                builder.setNegativeButton("Cancelar")
                { dialog, _ -> dialog.cancel() }
                builder.show()
            }
            R.id.weather ->{
                if(viewModel.weatherdata.value != null){
                    val intent = Intent(this,WeatherActivity::class.java)
                    startActivity(intent)
                }else{
                    Snackbar.make(binding.root,"The Weather data did not charge. Try again later", Snackbar.LENGTH_LONG).setBackgroundTint(
                        Color.RED).show()
                }
            }
            else -> super.onOptionsItemSelected(item)
        }

        return(super.onOptionsItemSelected(item));
    }

    private fun openSelectorActivityForResult(keyword:String) {
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
        }else{
            Snackbar.make(binding.root,"La movie ja existeix o ha sigut cancelada",Snackbar.LENGTH_LONG).setBackgroundTint(Color.RED).show()
        }
    }


}