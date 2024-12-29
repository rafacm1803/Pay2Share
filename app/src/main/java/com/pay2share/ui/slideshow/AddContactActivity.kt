package com.pay2share.ui.slideshow

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pay2share.R
import com.pay2share.database.DatabaseHelper
import com.pay2share.data.database.ContactRepository

class AddContactActivity : AppCompatActivity() {

    private lateinit var contactRepository: ContactRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        val dbHelper = DatabaseHelper(this)
        contactRepository = ContactRepository(dbHelper)

        val editTextEmail: EditText = findViewById(R.id.editTextContactEmail)
        val buttonAddContact: Button = findViewById(R.id.buttonAddContact)

        buttonAddContact.setOnClickListener {
            val email = editTextEmail.text.toString()
            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor, introduce un email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getInt("user_id", -1)

            contactRepository.addContact(userId, email)
            Toast.makeText(this, "Contacto añadido", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}