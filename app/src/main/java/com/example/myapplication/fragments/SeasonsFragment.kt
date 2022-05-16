package com.example.myapplication.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentSeasonsBinding


class SeasonsFragment : Fragment() {

    private var _binding: FragmentSeasonsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeasonsBinding.inflate(inflater, container, false)

        binding.actionBar.buttonRegularSeason.isSelected = true

        binding.actionBar.buttonRegularSeason.setOnClickListener {
            if (!it.isSelected) {
                it.apply { isSelected = !isSelected }
                binding.actionBar.buttonPlayoffs.apply { isSelected = !isSelected }
            }
        }
        binding.actionBar.buttonPlayoffs.setOnClickListener {
            if (!it.isSelected) {
                it.apply { isSelected = !isSelected }
                binding.actionBar.buttonRegularSeason.apply { isSelected = !isSelected }
            }
        }

        return binding.root
    }
}