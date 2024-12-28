package com.pay2share.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pay2share.databinding.ActivityLoginBinding
import com.pay2share.MainActivity
import com.pay2share.database.DatabaseHelper
import com.pay2share.data.database.UsuarioRepository
import com.pay2share.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var usuarioRepository: UsuarioRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dbHelper = DatabaseHelper(this)
        usuarioRepository = UsuarioRepository(dbHelper)

        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val cursor = usuarioRepository.obtenerUsuarioPorEmail(email)
            if (cursor != null && cursor.moveToFirst()) {
                // Email encontrado, iniciar sesión
                val userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val userName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                // Guardar sesión
                val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
                with(sharedPreferences.edit()) {
                    putInt("user_id", userId)
                    putString("user_name", userName)
                    putString("user_email", email)
                    apply()
                }
                cursor.close()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Email no encontrado, mostrar mensaje de error
                Toast.makeText(this, "Email no registrado", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}