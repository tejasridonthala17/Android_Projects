package com.example.layoutdemofragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MovieAdapter(private val movieList: List<Movie>) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val posterImg: ImageView = itemView.findViewById(R.id.moviePoster)
        val nameTxt: TextView = itemView.findViewById(R.id.movieName)
        val yearTxt: TextView = itemView.findViewById(R.id.movieYear)
        val descTxt: TextView = itemView.findViewById(R.id.movieDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.moviecard, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]
        holder.posterImg.setImageResource(movie.posterResId)
        holder.nameTxt.text = movie.name
        holder.yearTxt.text = movie.releaseYear.toString()
        holder.descTxt.text = movie.description
    }

    override fun getItemCount(): Int = movieList.size
}
