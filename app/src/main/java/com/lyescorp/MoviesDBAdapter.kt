package com.lyescorp

import android.app.Activity
import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lyescorp.models.Movie
import com.lyescorp.models.MovieResponse
import com.lyescorp.myfavouritemovies.R
import com.lyescorp.myfavouritemovies.databinding.MoviedbItemBinding
import com.lyescorp.server.RetrofitConnection.service
import kotlinx.coroutines.*


class MoviesDBAdapter(
    var movies: List<MovieResponse> = emptyList(),
    var mContext: Context,
) :

    RecyclerView.Adapter<MoviesDBAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.moviedb_item, parent, false), mContext)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
    }

    override fun getItemCount(): Int = movies.size

    class ViewHolder(view: View, val mContext: Context) : RecyclerView.ViewHolder(view) {

        private val binding = MoviedbItemBinding.bind(view)

        fun bind(movie: MovieResponse) {

            binding.title.text = movie.title
            binding.txtDate.text = movie.releaseDate
            binding.txtvwPopularity.text = movie.popularity.toString()

            val circularProgressDrawable = CircularProgressDrawable(mContext)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()

            val requestOptions = RequestOptions()
                .placeholder(circularProgressDrawable)
            Glide.with(binding.imvwMovieDb)
                .load( "https://image.tmdb.org/t/p/w500" + movie.posterPath)
                .apply(requestOptions)
                .into(binding.imvwMovieDb)
            binding.cardMoviedb.setOnClickListener {
                var input = EditText(mContext)
                input.setHint(R.string.intropoints)
                input.inputType = InputType.TYPE_CLASS_NUMBER
                val builder = MaterialAlertDialogBuilder(mContext)
                builder.setTitle(R.string.askquest)
                builder.setView(input)
                builder.setPositiveButton("Introducir")
                { _, _ ->
                    val scope = CoroutineScope(Dispatchers.Default)
                    var bul: Movie? = null;
                    // Lanzamos una corrutina
                    val job = scope.launch {
                       bul = service.getMovieBool(movie.id!!.toLong())
                    }

                    // Esperamos a que termine la corrutina
                    runBlocking {
                        job.join()
                    }


                    if( input.text.toString().toDouble() <= 10.0 && (movie.id!!.toLong() != bul?.id)){
                        var newMovieFav = Movie(
                            title = movie.title.toString()
                        )
                        newMovieFav.id = movie.id!!.toLong()
                        newMovieFav.adult = movie.adult!!
                        newMovieFav.voteAverage = input.text.toString().toDouble()
                        newMovieFav.posterPath =  "https://image.tmdb.org/t/p/w500" + movie.posterPath
                        newMovieFav.backdropPath = "https://image.tmdb.org/t/p/w500" + movie.backdropPath
                        newMovieFav.favorite = true
                        newMovieFav.genreIDS = movie.genreIds.map { it.toLong() }
                        newMovieFav.originalLanguage = movie.originalLanguage!!
                        newMovieFav.originalTitle = movie.originalTitle!!
                        newMovieFav.overview = movie.overview!!
                        newMovieFav.video = movie.video!!
                        newMovieFav.popularity = movie.popularity!!
                        newMovieFav.releaseDate = movie.releaseDate!!
                        newMovieFav.voteCount = movie.voteCount!!.toLong()
                        GlobalScope.launch{
                            service.newMovie(newMovieFav)
                        }
                        (mContext as Activity).setResult(Activity.RESULT_OK)
                        (mContext).finish()

                    }

                }
                builder.setNegativeButton("Cancelar")
                { dialog, _ -> dialog.cancel() }
                builder.show()
            }
        }





    }
}