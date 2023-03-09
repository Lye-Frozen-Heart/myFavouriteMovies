package com.mp08.myfavouritemovies

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.lyescorp.myfavouritemovies.R
import com.lyescorp.myfavouritemovies.databinding.ActivityMainBinding
import com.lyescorp.myfavouritemovies.databinding.WeatherActivityBinding
import com.mp08.myfavouritemovies.server.RetrofitConnection.service
import com.mp08.myfavouritemovies.viewmodels.MainViewModel
import com.mp08.myfavouritemovies.viewmodels.MainViewModelFactory


class WeatherActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels { MainViewModelFactory() }

    private lateinit var binding: WeatherActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Para trabajar con ViewBinding, igualamos el binding al Binding correspondiente de la activity, cada una tiene uno correspondiente
        //por defecto, seteando la view que toca
        binding = WeatherActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        viewModel.getWeatherResp()

        viewModel.weatherdata.observe(this){

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




}