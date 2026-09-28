package com.example.layoutdemofragment

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment

class TableFragment : Fragment() {

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

        val table = TableLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            isStretchAllColumns = true
        }

        val header = TableRow(requireContext()).apply {
            setBackgroundColor(Color.parseColor("#444444"))
            setPadding(12, 12, 12, 12)
        }
        header.addView(createTextView("Poster", true))
        header.addView(createTextView("Movie Info", true))
        table.addView(header)

        for (movie in movies) {
            val row = TableRow(requireContext()).apply {
                setPadding(8, 8, 8, 8)
                gravity = Gravity.CENTER_VERTICAL
            }
            val img = ImageView(requireContext()).apply {
                setImageResource(movie.posterResId)
                layoutParams = TableRow.LayoutParams(150, 200)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            val desc = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(24, 0, 0, 0)
                addView(createTextView(movie.name, true, 18f))
                addView(createTextView(movie.releaseYear.toString(), false, 14f, Color.LTGRAY))
            }
            row.addView(img)
            row.addView(desc)
            table.addView(row)
        }
        root.addView(table)
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
