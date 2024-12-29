package com.pay2share.data.database

import android.database.Cursor
import com.pay2share.database.DatabaseHelper

class ContactRepository(private val dbHelper: DatabaseHelper) {

    fun getContactsByUser(userId: Int): List<String> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT email FROM contacts WHERE user_id = ?", arrayOf(userId.toString()))
        val contacts = mutableListOf<String>()
        while (cursor.moveToNext()) {
            val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
            contacts.add(email)
        }
        cursor.close()
        return contacts
    }

    fun addContact(userId: Int, email: String): Long {
        return dbHelper.insertContact(userId, email)
    }

    fun deleteContact(userId: Int, email: String): Int {
        return dbHelper.deleteContact(userId, email)
    }
}