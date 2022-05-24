package com.example.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.myapplication.R
import com.example.myapplication.activities.PlayerDetailsActivity
import com.example.myapplication.databinding.FragmentPlayerDetailsBinding
import com.example.myapplication.helpers.TeamsHelper
import com.example.myapplication.models.Player
import com.example.myapplication.viewmodels.SharedViewModel

class PlayerDetailsFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var _binding: FragmentPlayerDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var player: Player

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerDetailsBinding.inflate(inflater, container, false)

        player = (activity as PlayerDetailsActivity).player
        setViews(player)

        return binding.root
    }

    private fun setViews(player: Player) {
        // Player Details Card
        binding.playerDetailsCard.position.text = player.fullPosition()
        binding.playerDetailsCard.bottomSubDetails.firstDetail.type.text =
            getString(R.string.height)
        binding.playerDetailsCard.bottomSubDetails.firstDetail.value.text =
            if (player.height_feet != null) player.heightImperial() else getString(R.string.n_a)
        binding.playerDetailsCard.bottomSubDetails.secondDetail.type.text =
            getString(R.string.weight)
        binding.playerDetailsCard.bottomSubDetails.secondDetail.value.text =
            if (player.weight_pounds != null) player.weight_pounds.toString() else getString(R.string.n_a)
        binding.playerDetailsCard.teamName.text = player.team.full_name
        val (logoId, colorId) = TeamsHelper().getLogoAndColor(player.team.name)
        binding.playerDetailsCard.teamLogo.image.setImageResource(logoId)
        binding.playerDetailsCard.teamLogo.backgroundCard.setCardBackgroundColor(ContextCompat.getColorStateList(requireContext(), colorId))
    }
}