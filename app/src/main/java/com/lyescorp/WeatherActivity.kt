package com.lyescorp

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.lyescorp.myfavouritemovies.R
import com.lyescorp.myfavouritemovies.databinding.WeatherActivityBinding

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class WeatherActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels { MainViewModelFactory() }
    private lateinit var binding: WeatherActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Hacemos bind de todos los campos, tardara un poco pero... Efectivo supongo
        binding = WeatherActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        //Titulo
        supportActionBar?.setTitle(R.string.titleweatheract)
        Snackbar.make(view,"Espere a que carguen los datos, disculpe las molestias",Snackbar.LENGTH_LONG).setBackgroundTint(
            Color.BLUE).show()
        binding.txtvwTime.text = viewModel.weatherdata.value?.location?.localtime
        binding.txtvwCountry.text =  viewModel.weatherdata.value?.location?.country
        binding.txtvwName.text =  viewModel.weatherdata.value?.location?.name
        binding.txtvwRegion.text =  viewModel.weatherdata.value?.location?.region
        binding.txtvwDegree.text =  viewModel.weatherdata.value?.current?.windDegree.toString()
        binding.txtvwDirection.text =  viewModel.weatherdata.value?.current?.windDir
        binding.txtvwLatit.text =  viewModel.weatherdata.value?.location?.lat.toString()
        binding.txtvwLongit.text =  viewModel.weatherdata.value?.location?.lon.toString()
        binding.txtvwTzId.text =  viewModel.weatherdata.value?.location?.tzId
        binding.txtvwlstupdepch.text =  viewModel.weatherdata.value?.current?.lastUpdatedEpoch.toString()
        binding.txtvwlstupdated.text =  viewModel.weatherdata.value?.current?.lastUpdated.toString()
        binding.txtvwTempC.text =  viewModel.weatherdata.value?.current?.tempC.toString()
        binding.txtvwTempF.text =  viewModel.weatherdata.value?.current?.tempF.toString()
        binding.txtvwWindMPH.text =  viewModel.weatherdata.value?.current?.windMph.toString()
        binding.txtvwWindKPH.text =  viewModel.weatherdata.value?.current?.windKph.toString()

        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        val requestOptions = RequestOptions()
            .placeholder(circularProgressDrawable)
        Glide.with(applicationContext)
            .load( "https:" + viewModel.weatherdata.value?.current?.condition?.icon)
            .apply(requestOptions)
            .into(binding.imvwWeatherIcon)

        var handler = Handler(Looper.getMainLooper())

        // Llama a la función getWeatherResponse y se actualizan los datos según la API... Quizás otra manera¿?
        //Función que recarga
        handler.postDelayed(object : Runnable {
            override fun run() {
                viewModel.getWeatherResp()
                binding.txtvwTime.text = viewModel.weatherdata.value?.location?.localtime
                binding.txtvwCountry.text =  viewModel.weatherdata.value?.location?.country
                binding.txtvwName.text =  viewModel.weatherdata.value?.location?.name
                binding.txtvwRegion.text =  viewModel.weatherdata.value?.location?.region
                binding.txtvwDegree.text =  viewModel.weatherdata.value?.current?.windDegree.toString()
                binding.txtvwDirection.text =  viewModel.weatherdata.value?.current?.windDir
                binding.txtvwLatit.text =  viewModel.weatherdata.value?.location?.lat.toString()
                binding.txtvwLongit.text =  viewModel.weatherdata.value?.location?.lon.toString()
                binding.txtvwTzId.text =  viewModel.weatherdata.value?.location?.tzId
                binding.txtvwlstupdepch.text =  viewModel.weatherdata.value?.current?.lastUpdatedEpoch.toString()
                binding.txtvwlstupdated.text =  viewModel.weatherdata.value?.current?.lastUpdated.toString()
                binding.txtvwTempC.text =  viewModel.weatherdata.value?.current?.tempC.toString()
                binding.txtvwTempF.text =  viewModel.weatherdata.value?.current?.tempF.toString()
                binding.txtvwWindMPH.text =  viewModel.weatherdata.value?.current?.windMph.toString()
                binding.txtvwWindKPH.text =  viewModel.weatherdata.value?.current?.windKph.toString()
                binding.edtLocation.setText(viewModel.weatherdata.value?.location?.name)
                val circularProgressDrawable = CircularProgressDrawable(this@WeatherActivity)
                circularProgressDrawable.strokeWidth = 5f
                circularProgressDrawable.centerRadius = 30f
                circularProgressDrawable.start()
                val requestOptions = RequestOptions()
                    .placeholder(circularProgressDrawable)
                Glide.with(applicationContext)
                    .load( "https:" + viewModel.weatherdata.value?.current?.condition?.icon)
                    .apply(requestOptions)
                    .into(binding.imvwWeatherIcon).onDestroy()
                handler.postDelayed(this, 10000)
            }
        }, 15000)
        // Nos suscribimos a cuando llega un error de la api. Esto peta el programa si no hay ninguna Movie
        viewModel.errorApiRest.observe(this) {
            if (it != null) {
                Snackbar.make(findViewById(android.R.id.content),
                    it,
                    Snackbar.LENGTH_LONG).show()
            }
        }
        binding.btnChangeLoc.setOnClickListener{
            if(binding.edtLocation.text.isNotBlank() || binding.edtLocation.text.isNotEmpty()) {
                GlobalScope.launch {
                    viewModel.updateConfig(binding.edtLocation.text.toString())
                }
            }else{
                Snackbar.make(view,"Introduzca un valor válido",Snackbar.LENGTH_LONG).setBackgroundTint(
                    Color.RED).show()
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}