package com.example.phonebook.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ThreadDao {
    @Query("SELECT * FROM threads ORDER BY lastMessageTime DESC")
    fun getAll(): LiveData<List<ConversationThread>>

    @Query("SELECT * FROM threads WHERE phoneNumber = :phone LIMIT 1")
    suspend fun findByPhone(phone: String): ConversationThread?

    @Query("SELECT * FROM threads WHERE threadId = :id LIMIT 1")
    suspend fun getById(id: Long): ConversationThread?

    @Insert
    suspend fun insert(thread: ConversationThread): Long

    @Update
    suspend fun update(thread: ConversationThread)

    @Delete
    suspend fun delete(thread: ConversationThread)
}
