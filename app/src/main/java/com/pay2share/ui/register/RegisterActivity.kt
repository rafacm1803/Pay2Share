package com.pay2share.ui.register

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pay2share.databinding.ActivityRegisterBinding
import com.pay2share.database.DatabaseHelper
import com.pay2share.data.database.UsuarioRepository

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dbHelper = DatabaseHelper(this)
        val usuarioRepository = UsuarioRepository(dbHelper)

        binding.buttonRegister.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val email = binding.editTextEmail.text.toString()

            // Verificar si el email ya está registrado
            val cursor = usuarioRepository.obtenerUsuarioPorEmail(email)
            if (cursor != null && cursor.moveToFirst()) {
                Toast.makeText(this, "Este email ya está registrado", Toast.LENGTH_SHORT).show()
                cursor.close()
                return@setOnClickListener
            }

            usuarioRepository.crearUsuario(name, email)
            Toast.makeText(this, "Registrado con éxito", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}