package com.pay2share.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pay2share.databinding.FragmentHomeBinding
import com.pay2share.database.DatabaseHelper
import com.pay2share.data.database.UsuarioRepository
import com.pay2share.data.database.GrupoRepository

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dbHelper = DatabaseHelper(requireContext())
        val usuarioRepository = UsuarioRepository(dbHelper)
        val grupoRepository = GrupoRepository(dbHelper)
        val homeViewModel = ViewModelProvider(this, HomeViewModelFactory(usuarioRepository)).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.grupos.observe(viewLifecycleOwner) { grupos ->
            textView.text = grupos.joinToString("\n")
        }

        // Suponiendo que el ID del usuario es 1
        homeViewModel.cargarGrupos(1)

        val buttonAddGroup: Button = binding.buttonAddGroup
        buttonAddGroup.setOnClickListener {
            // Añadir un grupo para el usuario con ID 1
            grupoRepository.crearGrupo("Nuevo Grupo")
            homeViewModel.cargarGrupos(1) // Recargar los grupos
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}