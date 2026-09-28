package com.example.layoutdemofragment

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment

class GridFragment : Fragment() {

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

        val grid = GridLayout(requireContext()).apply {
            columnCount = 2
            rowCount = (movies.size + 1) / 2
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        for (movie in movies) {
            val item = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(16, 16, 16, 16)
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                }

                val img = ImageView(requireContext()).apply {
                    setImageResource(movie.posterResId)
                    layoutParams = LinearLayout.LayoutParams(250, 350)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
                addView(img)
                addView(createTextView(movie.name, false, 14f).apply { gravity = Gravity.CENTER })
            }
            grid.addView(item)
        }
        root.addView(grid)
        scrollView.addView(root)
        return scrollView
    }

    private fun createTextView(text: String, bold: Boolean, size: Float = 16f, color: Int = Color.WHITE): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            this.textSize = size
            this.setTextColor(color)
            if (bold) this.setTypeface(null, android.graphics.Typeface.BOLD)
        }
    }
}
