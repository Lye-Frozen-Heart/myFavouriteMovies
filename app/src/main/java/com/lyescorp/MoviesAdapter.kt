package com.lyescorp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.lyescorp.models.Movie
import com.lyescorp.myfavouritemovies.R
import com.lyescorp.myfavouritemovies.databinding.MovieItemBinding



@Suppress("DEPRECATION")
class MoviesAdapter(var movies: List<Movie> = emptyList(),
                    private val mContext: Context,
                    private val onItemClicked: (Movie) -> Unit,
                    private val onDeleteClicked: (Movie) -> Unit,
                ) :
    RecyclerView.Adapter<MoviesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.movie_item, parent, false), mContext)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
        val cont = holder.itemView.context
        // para capturar al imagen de borrar
        val binding = MovieItemBinding.bind(holder.itemView)
        binding.delete.setOnClickListener { onDeleteClicked(movie) }
        holder.itemView.setOnClickListener{
            onItemClicked(movie)
        }

    }



    override fun getItemCount(): Int = movies.size

    class ViewHolder(val view: View, val mContext: Context) : RecyclerView.ViewHolder(view) {

        private val binding = MovieItemBinding.bind(view)

        fun bind(movie: Movie) {
            binding.title.text = movie.title
            binding.txtvwVotes.text = movie.voteAverage.toString()
            binding.txtvwReleaseDate.text = movie.releaseDate

            // el spinner
            val circularProgressDrawable = CircularProgressDrawable(mContext)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()

            // cache de imagenes en disco
            // Si la imagen original es modificada Glide no se entera
            val requestOptions = RequestOptions()
                //.diskCacheStrategy(DiskCacheStrategy.ALL) // cache
               // .circleCrop() // redonda
                .placeholder(circularProgressDrawable)  // loading
                //.timeout(10000)

            Glide.with(binding.thumb)
                .load(movie.posterPath)
                .apply(requestOptions)
                .into(binding.thumb)

            // Llamada sin usar cache
            // Glide.with(binding.thumb).load(movie.posterPath).into(binding.thumb)

            binding.delete.setImageResource(R.drawable.ic_delete)

        }
    }

}