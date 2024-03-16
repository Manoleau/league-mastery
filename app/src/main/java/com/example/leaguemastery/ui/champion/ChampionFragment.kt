package com.example.leaguemastery.ui.champion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.leaguemastery.databinding.FragmentChampionBinding

class ChampionFragment : Fragment() {

    private var _binding: FragmentChampionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChampionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}