package com.pay2share.ui.group

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.pay2share.R
import com.pay2share.database.DatabaseHelper
import com.pay2share.data.database.GrupoRepository
import com.pay2share.data.database.UsuarioRepository
import com.pay2share.ui.home.HomeFragment

class CreateGroupActivity : AppCompatActivity() {

    private lateinit var grupoRepository: GrupoRepository
    private lateinit var usuarioRepository: UsuarioRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        val dbHelper = DatabaseHelper(this)
        grupoRepository = GrupoRepository(dbHelper)
        usuarioRepository = UsuarioRepository(dbHelper)

        val editTextGroupName: EditText = findViewById(R.id.editTextGroupName)
        val buttonCreateGroup: Button = findViewById(R.id.buttonCreateGroup)

        buttonCreateGroup.setOnClickListener {
            val groupName = editTextGroupName.text.toString()
            val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
            val userId = sharedPreferences.getInt("user_id", -1)

            if (userId != -1) {
                val groupId = grupoRepository.crearGrupo(groupName, userId)
                usuarioRepository.anyadirUsuarioAGrupo(userId, groupId.toInt())
                val intent = Intent(this, HomeFragment::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}