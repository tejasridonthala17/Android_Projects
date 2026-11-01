package com.example.phonebook.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ContactDao {

    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAll(): LiveData<List<Contact>>

    @Query("SELECT * FROM contacts ORDER BY name ASC")
    suspend fun getAllOnce(): List<Contact>

    @Query("SELECT * FROM contacts WHERE contactId = :id")
    suspend fun getById(id: Long): Contact?

    // Used for dedupe: check if a phone number already exists
    @Query("SELECT * FROM contacts WHERE phoneNumber = :phone LIMIT 1")
    suspend fun findByPhone(phone: String): Contact?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(contact: Contact): Long

    @Update
    suspend fun update(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)

    @Query("DELETE FROM contacts WHERE contactId = :id")
    suspend fun deleteById(id: Long)
}
