package com.example.retrofitdemo

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private var allPosts: List<PostObject> = emptyList()
    private lateinit var postsContainer: LinearLayout
    private lateinit var userIdSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        postsContainer = findViewById(R.id.postsContainer)
        userIdSpinner = findViewById(R.id.userIdSpinner)
        val addButton = findViewById<Button>(R.id.addButton)

        // Populate Spinner options: "All" and IDs 1 to 10
        val spinnerOptions = arrayListOf("All")
        for (i in 1..10) {
            spinnerOptions.add(i.toString())
        }
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        userIdSpinner.adapter = adapter

        // ADD DATA click listener
        addButton.setOnClickListener {
            val intent = Intent(this, AddPostActivity::class.java)
            startActivity(intent)
        }

        // Spinner item selection listener
        userIdSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterAndDisplayPosts()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onResume() {
        super.onResume()
        // Automatically fetch data when activity displays or resumes
        loadData()
    }

    private fun loadData() {
        RetrofitClient.api.getPosts().enqueue(object : Callback<List<PostObject>> {
            override fun onResponse(call: Call<List<PostObject>>, response: Response<List<PostObject>>) {
                if (response.isSuccessful) {
                    allPosts = response.body() ?: emptyList()
                    Log.d("RETROFIT", "Loaded ${allPosts.size} posts")
                    filterAndDisplayPosts()
                } else {
                    Log.e("RETROFIT", "HTTP Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<PostObject>>, t: Throwable) {
                Log.e("RETROFIT", "GET request failed", t)
            }
        })
    }

    private fun filterAndDisplayPosts() {
        postsContainer.removeAllViews()
        
        val selectedOption = userIdSpinner.selectedItem?.toString() ?: "All"
        
        val filteredPosts = if (selectedOption == "All") {
            allPosts
        } else {
            val selectedUserId = selectedOption.toIntOrNull()
            if (selectedUserId != null) {
                allPosts.filter { it.userId == selectedUserId }
            } else {
                allPosts
            }
        }

        filteredPosts.forEach { post ->
            val rowLayout = LinearLayout(this)
            rowLayout.orientation = LinearLayout.HORIZONTAL
            rowLayout.setPadding(0, 15, 0, 15)
            rowLayout.isClickable = true
            rowLayout.setFocusable(true)

            // Dynamic background ripple selection effect
            val outValue = android.util.TypedValue()
            theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
            rowLayout.setBackgroundResource(outValue.resourceId)

            val idTextView = TextView(this).apply {
                text = post.id.toString()
                textSize = 14f
            }
            val userIdTextView = TextView(this).apply {
                text = post.userId.toString()
                textSize = 14f
            }
            val titleTextView = TextView(this).apply {
                text = post.title
                textSize = 14f
            }
            val bodyTextView = TextView(this).apply {
                text = post.body
                textSize = 14f
            }

            rowLayout.addView(idTextView, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
            rowLayout.addView(userIdTextView, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f))
            rowLayout.addView(titleTextView, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3f))
            rowLayout.addView(bodyTextView, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3f))

            // When clicked on any row / column element, show full details
            rowLayout.setOnClickListener {
                showPostDetailsDialog(post)
            }

            postsContainer.addView(rowLayout)
        }
    }

    private fun showPostDetailsDialog(post: PostObject) {
        val detailsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 30, 40, 30)
        }

        val detailsText = """
            ID: ${post.id}
            
            USER ID: ${post.userId}
            
            TITLE:
            ${post.title}
            
            BODY:
            ${post.body}
        """.trimIndent()

        val textView = TextView(this).apply {
            text = detailsText
            textSize = 16f
            setTextColor(android.graphics.Color.BLACK)
        }
        
        detailsLayout.addView(textView)

        AlertDialog.Builder(this)
            .setTitle("Post Details")
            .setView(detailsLayout)
            .setPositiveButton("OK", null)
            .show()
    }
}
