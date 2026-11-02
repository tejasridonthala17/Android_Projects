package com.example.retrofitdemo

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        val userIdEditText = findViewById<EditText>(R.id.userIdEditText)
        val titleEditText = findViewById<EditText>(R.id.titleEditText)
        val bodyEditText = findViewById<EditText>(R.id.bodyEditText)
        val submitButton = findViewById<Button>(R.id.submitButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)

        // CANCEL
        cancelButton.setOnClickListener {
            finish()
        }

        // SUBMIT
        submitButton.setOnClickListener {
            val userIdText = userIdEditText.text.toString()
            val title = titleEditText.text.toString()
            val body = bodyEditText.text.toString()

            // Simple validation
            if (userIdText.isBlank() || title.isBlank() || body.isBlank()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = userIdText.toInt()
            // Create POST object
            val newPost = CreatePostObject(
                userId = userId,
                title = title,
                body = body
            )

            // POST request
            RetrofitClient.api.createPost(newPost).enqueue(object : Callback<PostObject> {
                override fun onResponse(call: Call<PostObject>, response: Response<PostObject>) {
                    if (response.isSuccessful) {
                        val createdPost = response.body()
                        Log.d("POST", "POST successful")
                        Log.d("POST", "Created ID: ${createdPost?.id}")
                        Log.d("POST", "Title: ${createdPost?.title}")
                        Toast.makeText(this@AddPostActivity, "Data submitted successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Log.e("POST", "HTTP Error: ${response.code()}")
                        Toast.makeText(this@AddPostActivity, "POST failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PostObject>, t: Throwable) {
                    Log.e("POST", "Request failed", t)
                    Toast.makeText(this@AddPostActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
