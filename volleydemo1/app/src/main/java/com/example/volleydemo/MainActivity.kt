package com.example.volleydemo

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.volleydemo.databinding.ActivityMainBinding
import com.example.volleydemo.databinding.DialogMovieDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var movieAdapter: MovieAdapter
    private val movieList = mutableListOf<Movie>()

    private val apiKey = "trilogy"
    private var currentQuery = ""
    private var currentPage = 1
    private var totalResults = 0

    companion object {
        private const val TAG = "MovieSearchEngine"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()

        // Initial state: Start empty without auto-loading Avengers
        showInitialState()
    }

    private fun showInitialState() {
        binding.etSearch.text?.clear()
        binding.tvSearchMessage.text = "Search for movies, series, and more..."
        binding.tvSearchMessage.visibility = View.VISIBLE
        binding.tvResultCount.visibility = View.GONE
        binding.btnLoadMore.visibility = View.GONE
        binding.pbSearch.visibility = View.GONE
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(movieList) { movie ->
            showMovieDetailsDialog(movie.imdbID)
        }
        // 2-column GridLayoutManager as required by problem statement
        binding.rvMovies.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = movieAdapter
        }
    }

    private fun setupListeners() {
        binding.btnSearch.setOnClickListener {
            val query = binding.etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                performNewSearch(query)
            } else {
                Toast.makeText(this, "Please enter a movie title", Toast.LENGTH_SHORT).show()
            }
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.etSearch.text.toString().trim()
                if (query.isNotEmpty()) {
                    performNewSearch(query)
                }
                true
            } else {
                false
            }
        }

        binding.btnLoadMore.setOnClickListener {
            if (currentQuery.isNotEmpty()) {
                currentPage++
                searchMovies(currentQuery, isNewSearch = false)
            }
        }
    }

    private fun isRetrofitSelected(): Boolean {
        return binding.rbRetrofit.isChecked
    }

    private fun performNewSearch(query: String) {
        hideKeyboard()
        currentQuery = query
        currentPage = 169999999999998
        movieList.clear()
        movieAdapter.notifyDataSetChanged()

        binding.pbSearch.visibility = View.VISIBLE
        binding.tvSearchMessage.visibility = View.GONE
        binding.tvResultCount.visibility = View.GONE
        binding.btnLoadMore.visibility = View.GONE

        val selectedMethod = if (isRetrofitSelected()) "Retrofit" else "Volley"
        Log.d(TAG, "----------------------------------------")
        Log.d(TAG, "Initiating new search for query: '$query'")
        Log.d(TAG, "Selected networking method: $selectedMethod")

        searchMovies(query, isNewSearch = true)
    }

    private fun searchMovies(query: String, isNewSearch: Boolean) {
        if (isRetrofitSelected()) {
            searchMoviesWithRetrofit(query, isNewSearch)
        } else {
            searchMoviesWithVolley(query, isNewSearch)
        }
    }

    // =========================================================================
    // RETROFIT NETWORKING METHOD
    // =========================================================================
    private fun searchMoviesWithRetrofit(query: String, isNewSearch: Boolean) {
        if (!isNewSearch) {
            binding.btnLoadMore.isEnabled = false
            binding.btnLoadMore.text = "Loading..."
        }

        val requestUrl = "https://www.omdbapi.com/?apikey=$apiKey&s=$query&page=$currentPage"
        Log.d(TAG, "[Retrofit] Request URL: $requestUrl (Page $currentPage)")

        RetrofitClient.instance.searchMovies(apiKey, query, currentPage)
            .enqueue(object : Callback<OmdbSearchResponse> {
                override fun onResponse(
                    call: Call<OmdbSearchResponse>,
                    response: Response<OmdbSearchResponse>
                ) {
                    binding.pbSearch.visibility = View.GONE
                    binding.btnLoadMore.isEnabled = true
                    binding.btnLoadMore.text = getString(R.string.load_more)

                    if (response.isSuccessful) {
                        val body = response.body()
                        val isSuccess = body?.response?.equals("True", ignoreCase = true) == true

                        if (isSuccess && body != null) {
                            totalResults = body.totalResults?.toIntOrNull() ?: 0
                            Log.d(TAG, "[Retrofit] Response successful. Total results: $totalResults")

                            val searchItems = body.search ?: emptyList()
                            val newMovies = searchItems.map { item ->
                                Movie(
                                    title = item.title ?: "Unknown",
                                    year = item.year ?: "N/A",
                                    imdbID = item.imdbID ?: "",
                                    type = item.type ?: "movie",
                                    posterUrl = item.poster ?: "N/A"
                                )
                            }

                            handleSearchResultsSuccess(newMovies, isNewSearch, query)
                        } else {
                            val errorMsg = body?.error ?: "No movies found."
                            Log.e(TAG, "[Retrofit] API returned error: $errorMsg")
                            handleSearchResultsError(errorMsg, isNewSearch)
                        }
                    } else {
                        val httpError = "HTTP Error ${response.code()}"
                        Log.e(TAG, "[Retrofit] $httpError")
                        handleSearchResultsError(httpError, isNewSearch)
                    }
                }

                override fun onFailure(call: Call<OmdbSearchResponse>, t: Throwable) {
                    binding.pbSearch.visibility = View.GONE
                    binding.btnLoadMore.isEnabled = true
                    binding.btnLoadMore.text = getString(R.string.load_more)

                    val errorMsg = t.localizedMessage ?: "Network error"
                    Log.e(TAG, "[Retrofit] Failure: $errorMsg", t)
                    handleSearchResultsError(errorMsg, isNewSearch)
                }
            })
    }

    private fun fetchMovieDetailsWithRetrofit(
        imdbID: String,
        dialogBinding: DialogMovieDetailBinding
    ) {
        val requestUrl = "https://www.omdbapi.com/?apikey=$apiKey&i=$imdbID&plot=full"
        Log.d(TAG, "[Retrofit] Detail Request URL: $requestUrl")

        RetrofitClient.instance.getMovieDetails(apiKey, imdbID)
            .enqueue(object : Callback<OmdbDetailResponse> {
                override fun onResponse(
                    call: Call<OmdbDetailResponse>,
                    response: Response<OmdbDetailResponse>
                ) {
                    dialogBinding.pbDialogDetail.visibility = View.GONE

                    if (response.isSuccessful) {
                        val details = response.body()
                        if (details?.response?.equals("True", ignoreCase = true) == true) {
                            Log.d(TAG, "[Retrofit] Detail response received for: ${details.title}")
                            populateDialogDetails(
                                dialogBinding = dialogBinding,
                                title = details.title ?: "N/A",
                                year = details.year ?: "N/A",
                                runtime = details.runtime ?: "N/A",
                                genre = details.genre ?: "N/A",
                                imdbRating = details.imdbRating ?: "N/A",
                                imdbVotes = details.imdbVotes ?: "N/A",
                                director = details.director ?: "N/A",
                                actors = details.actors ?: "N/A",
                                plot = details.plot ?: "N/A",
                                posterUrl = details.poster ?: "N/A"
                            )
                        } else {
                            val err = details?.error ?: "Failed to load movie details"
                            Log.e(TAG, "[Retrofit] Detail error: $err")
                            Toast.makeText(this@MainActivity, err, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e(TAG, "[Retrofit] Detail HTTP Error: ${response.code()}")
                        Toast.makeText(this@MainActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<OmdbDetailResponse>, t: Throwable) {
                    dialogBinding.pbDialogDetail.visibility = View.GONE
                    Log.e(TAG, "[Retrofit] Detail failure: ${t.localizedMessage}")
                    Toast.makeText(this@MainActivity, t.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            })
    }

    // =========================================================================
    // VOLLEY NETWORKING METHOD
    // =========================================================================
    private fun searchMoviesWithVolley(query: String, isNewSearch: Boolean) {
        if (!isNewSearch) {
            binding.btnLoadMore.isEnabled = false
            binding.btnLoadMore.text = "Loading..."
        }

        val requestUrl = "https://www.omdbapi.com/?apikey=$apiKey&s=${Uri.encode(query)}&page=$currentPage"
        Log.d(TAG, "[Volley] Request URL: $requestUrl (Page $currentPage)")

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            requestUrl,
            null,
            { response ->
                binding.pbSearch.visibility = View.GONE
                binding.btnLoadMore.isEnabled = true
                binding.btnLoadMore.text = getString(R.string.load_more)

                val responseStatus = response.optString("Response", "False")
                if (responseStatus.equals("True", ignoreCase = true)) {
                    val totalStr = response.optString("totalResults", "0")
                    totalResults = totalStr.toIntOrNull() ?: 0
                    Log.d(TAG, "[Volley] Response successful. Total results: $totalResults")

                    val searchArray = response.optJSONArray("Search")
                    val newMovies = mutableListOf<Movie>()

                    if (searchArray != null) {
                        for (i in 0 until searchArray.length()) {
                            val item = searchArray.getJSONObject(i)
                            newMovies.add(
                                Movie(
                                    title = item.optString("Title", "Unknown"),
                                    year = item.optString("Year", "N/A"),
                                    imdbID = item.optString("imdbID", ""),
                                    type = item.optString("Type", "movie"),
                                    posterUrl = item.optString("Poster", "N/A")
                                )
                            )
                        }
                    }

                    handleSearchResultsSuccess(newMovies, isNewSearch, query)
                } else {
                    val errorMsg = response.optString("Error", "No movies found.")
                    Log.e(TAG, "[Volley] API returned error: $errorMsg")
                    handleSearchResultsError(errorMsg, isNewSearch)
                }
            },
            { error ->
                binding.pbSearch.visibility = View.GONE
                binding.btnLoadMore.isEnabled = true
                binding.btnLoadMore.text = getString(R.string.load_more)

                val errorMsg = error.localizedMessage ?: "Volley network error"
                Log.e(TAG, "[Volley] Error: $errorMsg", error)
                handleSearchResultsError(errorMsg, isNewSearch)
            }
        )

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    private fun fetchMovieDetailsWithVolley(
        imdbID: String,
        dialogBinding: DialogMovieDetailBinding
    ) {
        val requestUrl = "https://www.omdbapi.com/?apikey=$apiKey&i=$imdbID&plot=full"
        Log.d(TAG, "[Volley] Detail Request URL: $requestUrl")

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            requestUrl,
            null,
            { response ->
                dialogBinding.pbDialogDetail.visibility = View.GONE

                val responseStatus = response.optString("Response", "False")
                if (responseStatus.equals("True", ignoreCase = true)) {
                    val title = response.optString("Title", "N/A")
                    Log.d(TAG, "[Volley] Detail response received for: $title")

                    populateDialogDetails(
                        dialogBinding = dialogBinding,
                        title = title,
                        year = response.optString("Year", "N/A"),
                        runtime = response.optString("Runtime", "N/A"),
                        genre = response.optString("Genre", "N/A"),
                        imdbRating = response.optString("imdbRating", "N/A"),
                        imdbVotes = response.optString("imdbVotes", "N/A"),
                        director = response.optString("Director", "N/A"),
                        actors = response.optString("Actors", "N/A"),
                        plot = response.optString("Plot", "N/A"),
                        posterUrl = response.optString("Poster", "N/A")
                    )
                } else {
                    val errorMsg = response.optString("Error", "Failed to load movie details")
                    Log.e(TAG, "[Volley] Detail Error: $errorMsg")
                    Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                dialogBinding.pbDialogDetail.visibility = View.GONE
                val errorMsg = error.localizedMessage ?: "Network error loading details"
                Log.e(TAG, "[Volley] Detail Error: $errorMsg", error)
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
            }
        )

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    // =========================================================================
    // SHARED SEARCH RESULT HANDLERS
    // =========================================================================
    private fun handleSearchResultsSuccess(
        newMovies: List<Movie>,
        isNewSearch: Boolean,
        query: String
    ) {
        if (isNewSearch) {
            movieAdapter.setMovies(newMovies)
        } else {
            movieAdapter.addMovies(newMovies)
        }

        binding.tvResultCount.text = getString(R.string.found_results, totalResults, query)
        binding.tvResultCount.visibility = View.VISIBLE

        if (movieList.size < totalResults) {
            binding.btnLoadMore.visibility = View.VISIBLE
        } else {
            binding.btnLoadMore.visibility = View.GONE
        }
    }

    private fun handleSearchResultsError(message: String, isNewSearch: Boolean) {
        if (isNewSearch) {
            binding.tvSearchMessage.text = message
            binding.tvSearchMessage.visibility = View.VISIBLE
            binding.tvResultCount.visibility = View.GONE
            binding.btnLoadMore.visibility = View.GONE
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            binding.btnLoadMore.visibility = View.GONE
        }
    }

    // =========================================================================
    // MOVIE DETAILS DIALOG (As required: Clicking poster opens a Dialog)
    // =========================================================================
    private fun showMovieDetailsDialog(imdbID: String) {
        hideKeyboard()

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogBinding = DialogMovieDetailBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                (resources.displayMetrics.widthPixels * 0.90).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        dialogBinding.svDialogContent.visibility = View.GONE
        dialogBinding.pbDialogDetail.visibility = View.VISIBLE

        dialogBinding.btnCloseDialog.setOnClickListener {
            dialog.dismiss()
        }

        val selectedMethod = if (isRetrofitSelected()) "Retrofit" else "Volley"
        Log.d(TAG, "Opening details Dialog for imdbID: $imdbID using $selectedMethod")

        if (isRetrofitSelected()) {
            fetchMovieDetailsWithRetrofit(imdbID, dialogBinding)
        } else {
            fetchMovieDetailsWithVolley(imdbID, dialogBinding)
        }

        dialog.show()
    }

    private fun populateDialogDetails(
        dialogBinding: DialogMovieDetailBinding,
        title: String,
        year: String,
        runtime: String,
        genre: String,
        imdbRating: String,
        imdbVotes: String,
        director: String,
        actors: String,
        plot: String,
        posterUrl: String
    ) {
        dialogBinding.tvDialogTitle.text = title
        dialogBinding.tvDialogYear.text = getString(R.string.year_label, year)
        dialogBinding.tvDialogRuntime.text = getString(R.string.runtime_label, runtime)
        dialogBinding.tvDialogGenre.text = getString(R.string.genre_label, genre)

        val ratingPrefix = "IMDb Rating: "
        val ratingBuilder = SpannableStringBuilder(ratingPrefix + imdbRating)
        val orangeColor = Color.parseColor("#FF6F00")
        ratingBuilder.setSpan(
            ForegroundColorSpan(orangeColor),
            ratingPrefix.length,
            ratingBuilder.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        ratingBuilder.setSpan(
            StyleSpan(Typeface.BOLD),
            ratingPrefix.length,
            ratingBuilder.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        dialogBinding.tvDialogRating.text = ratingBuilder

        dialogBinding.tvDialogVotes.text = getString(R.string.imdb_votes_label, imdbVotes)
        dialogBinding.tvDialogDirector.text = getString(R.string.director_label, director)
        dialogBinding.tvDialogActors.text = getString(R.string.actors_label, actors)
        dialogBinding.tvDialogPlot.text = plot

        val imageLoader = VolleySingleton.getInstance(this).imageLoader
        if (posterUrl.isNotEmpty() && posterUrl != "N/A") {
            dialogBinding.ivDialogPoster.setDefaultImageResId(R.drawable.ic_movie_placeholder)
            dialogBinding.ivDialogPoster.setErrorImageResId(R.drawable.ic_movie_placeholder)
            dialogBinding.ivDialogPoster.setImageUrl(posterUrl, imageLoader)
        } else {
            dialogBinding.ivDialogPoster.setDefaultImageResId(R.drawable.ic_movie_placeholder)
            dialogBinding.ivDialogPoster.setImageUrl(null, imageLoader)
        }

        dialogBinding.svDialogContent.visibility = View.VISIBLE
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}