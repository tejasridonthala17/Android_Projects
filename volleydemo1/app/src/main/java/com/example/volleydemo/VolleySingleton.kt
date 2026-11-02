package com.example.volleydemo

import android.content.Context
import android.graphics.Bitmap
import androidx.collection.LruCache
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader

import com.android.volley.toolbox.Volley

class VolleySingleton private constructor(context: Context) {

    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    val imageLoader: ImageLoader by lazy {
        ImageLoader(requestQueue, object : ImageLoader.ImageCache {
            private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
            private val cacheSize = maxMemory / 8

            private val cache = object : LruCache<String, Bitmap>(cacheSize) {
                override fun sizeOf(key: String, bitmap: Bitmap): Int {
                    return bitmap.byteCount / 1024
                }
            }

            override fun getBitmap(url: String): Bitmap? {
                return cache.get(url)
            }

            override fun putBitmap(url: String, bitmap: Bitmap) {
                cache.put(url, bitmap)
            }
        })
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }

    companion object {
        @Volatile
        private var INSTANCE: VolleySingleton? = null

        fun getInstance(context: Context): VolleySingleton {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: VolleySingleton(context).also { INSTANCE = it }
            }
        }
    }
}