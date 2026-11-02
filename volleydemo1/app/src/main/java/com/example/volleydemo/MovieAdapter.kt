package com.example.volleydemo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.volleydemo.databinding.ItemMovieBinding
import java.util.Locale

class MovieAdapter(
    private val movieList: MutableList<Movie>,
    private val onMovieClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]
        val context = holder.itemView.context

        holder.binding.tvMovieTitle.text = movie.title

        val formattedType = movie.type.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
        holder.binding.tvMovieYearType.text = context.getString(
            R.string.year_type,
            movie.year,
            formattedType
        )

        holder.binding.tvMovieImdbId.text = context.getString(
            R.string.imdb_id,
            movie.imdbID
        )

        val imageLoader = VolleySingleton.getInstance(context).imageLoader
        if (movie.posterUrl != "N/A" && movie.posterUrl.isNotEmpty()) {
            holder.binding.ivPoster.setDefaultImageResId(R.drawable.ic_movie_placeholder)
            holder.binding.ivPoster.setErrorImageResId(R.drawable.ic_movie_placeholder)
            holder.binding.ivPoster.setImageUrl(movie.posterUrl, imageLoader)
        } else {
            holder.binding.ivPoster.setDefaultImageResId(R.drawable.ic_movie_placeholder)
            holder.binding.ivPoster.setImageUrl(null, imageLoader)
        }

        holder.itemView.setOnClickListener {
            onMovieClick(movie)
        }
    }

    override fun getItemCount(): Int = movieList.size

    fun addMovies(newMovies: List<Movie>) {
        val startPosition = movieList.size
        movieList.addAll(newMovies)
        notifyItemRangeInserted(startPosition, newMovies.size)
    }

    fun setMovies(newMovies: List<Movie>) {
        movieList.clear()
        movieList.addAll(newMovies)
        notifyDataSetChanged()
    }
}