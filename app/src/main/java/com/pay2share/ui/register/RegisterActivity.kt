package com.pay2share.ui.register

import android.os.Bundle
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
            usuarioRepository.crearUsuario(name, email)

        }
    }
}