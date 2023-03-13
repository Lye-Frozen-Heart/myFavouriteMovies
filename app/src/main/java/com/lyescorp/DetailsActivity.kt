package com.lyescorp

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.lyescorp.models.Movie
import com.lyescorp.myfavouritemovies.R
import com.lyescorp.myfavouritemovies.databinding.ActivityDetailsBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@Suppress("DEPRECATION")
class DetailsActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory() }
    private lateinit var binding: ActivityDetailsBinding
    private val adapter = MoviesAdapter(emptyList(), this,
        {viewModel.onMovieClicked(it) },
        {viewModel.onMovieDelete(it)})

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        supportActionBar?.setTitle(R.string.titledetails)

        val movie = intent.getSerializableExtra("movie") as Movie
        binding.txtvwTitle.text = movie.title

        //Img setter
        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        val requestOptions = RequestOptions()
            .placeholder(circularProgressDrawable)

        Glide.with(binding.imvwDetails)
            .load(movie.posterPath)
            .apply(requestOptions)
            .into(binding.imvwDetails)

        //Set fields
        binding.edtPoints.setText(movie.voteAverage.toString())
        binding.txtvwOverview.text = movie.overview
        binding.txtvwReleased.text = movie.releaseDate
        binding.txtvwPopularityD.text = movie.popularity.toString()
        binding.txtvwVotesD.text = movie.voteCount.toString()


        binding.btnUpdate.setOnClickListener{
            if(binding.edtPoints.text.isBlank() || binding.edtPoints.text.isEmpty() || binding.edtPoints.text.toString().toDouble() > 10){
                Snackbar.make(binding.root,"You should place a valid value for the points first (0 to 10)",Snackbar.LENGTH_LONG).setBackgroundTint(
                    Color.RED).show()
            }else{
                movie.voteAverage = binding.edtPoints.text.toString().toDouble()
               GlobalScope.launch {
                   viewModel.onMovieClicked(movie)
               }
                var pos = adapter.movies.indexOf(movie)
                adapter.notifyItemChanged(pos)
                setResult(Activity.RESULT_OK)
                Snackbar.make(binding.root,"Your movie has been updated!",Snackbar.LENGTH_LONG).setBackgroundTint(
                    Color.BLUE).show()
                }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)finish()
        return super.onOptionsItemSelected(item)
    }

}