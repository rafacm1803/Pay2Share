package com.pay2share.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pay2share.databinding.FragmentHomeBinding
import com.pay2share.database.DatabaseHelper
import com.pay2share.data.database.GrupoRepository
import com.pay2share.data.database.UsuarioRepository
import com.pay2share.ui.group.GroupDetailActivity

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

        val listView: ListView = binding.listViewGroups
        homeViewModel.grupos.observe(viewLifecycleOwner) { grupos ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, grupos.map { it.name })
            listView.adapter = adapter
        }

        // Obtener el ID del usuario de la sesión
        val sharedPreferences = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)

        homeViewModel.cargarGrupos(userId)

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val groupId = homeViewModel.grupos.value?.get(position)?.id ?: return@OnItemClickListener
            val intent = Intent(requireContext(), GroupDetailActivity::class.java)
            intent.putExtra("GROUP_ID", groupId)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}