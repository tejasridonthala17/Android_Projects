package com.example.layoutdemofragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment

class CardFragment : Fragment() {

    private val movies = MovieData.movies

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val scrollView = ScrollView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val root = LinearLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        for (movie in movies) {
            val card = inflater.inflate(R.layout.moviecard, root, false)
            card.findViewById<ImageView>(R.id.moviePoster).setImageResource(movie.posterResId)
            card.findViewById<TextView>(R.id.movieName).text = movie.name
            card.findViewById<TextView>(R.id.movieYear).text = movie.releaseYear.toString()
            card.findViewById<TextView>(R.id.movieDescription).text = movie.description
            root.addView(card)
        }
        scrollView.addView(root)
        return scrollView
    }
}
