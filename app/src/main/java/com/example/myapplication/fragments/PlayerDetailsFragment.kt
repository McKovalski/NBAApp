package com.example.myapplication.fragments

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.myapplication.R
import com.example.myapplication.activities.PlayerDetailsActivity
import com.example.myapplication.adapters.ImagePagerAdapter
import com.example.myapplication.databinding.AboutCardBinding.inflate
import com.example.myapplication.databinding.AddImageBottomSheetLayoutBinding
import com.example.myapplication.databinding.FragmentPlayerDetailsBinding
import com.example.myapplication.helpers.TeamsHelper
import com.example.myapplication.models.Player
import com.example.myapplication.models.PlayerImage
import com.example.myapplication.viewmodels.SharedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class PlayerDetailsFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var _binding: FragmentPlayerDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var player: Player
    private val images = mutableListOf<PlayerImage>()
    private var favouriteImage: PlayerImage? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerDetailsBinding.inflate(inflater, container, false)

        player = (activity as PlayerDetailsActivity).player

        val imagePagerAdapter = ImagePagerAdapter(requireContext(), mutableListOf())
        binding.playerDetailsCard.viewPager.adapter = imagePagerAdapter
        binding.playerDetailsCard.circleIndicator.setViewPager(binding.playerDetailsCard.viewPager)

        sharedViewModel.getPlayerImages(player.id)
        sharedViewModel.getPlayerFavouriteImage(requireContext(), player.id)
        sharedViewModel.playerImages.observe(viewLifecycleOwner) {
            images.addAll(it.filter { image -> image.playerId == player.id && !images.contains(image) })
            sharedViewModel.playerFavouriteImage.observe(viewLifecycleOwner) { favImage ->
                if (favImage != null) {
                    favouriteImage = favImage
                } else {
                    if (images.isNotEmpty()) {
                        favouriteImage = images[0]
                        sharedViewModel.setPlayerFavouriteImage(requireContext(), favouriteImage!!)
                    }
                }
            }
            val favouriteImagePosition = images.indexOf(favouriteImage)
            imagePagerAdapter.updateImages(images)
            if (favouriteImagePosition >= 0) {
                binding.playerDetailsCard.viewPager.currentItem = favouriteImagePosition
            } else {
                binding.playerDetailsCard.viewPager.currentItem = 0
            }
            binding.playerDetailsCard.viewPager.adapter = imagePagerAdapter
            binding.playerDetailsCard.circleIndicator.setViewPager(binding.playerDetailsCard.viewPager)
        }

        setViews(player)
        setBottomSheetDialog()

        return binding.root
    }

    private fun setBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.add_image_bottom_sheet_layout, null)
        val bottomSheetBinding = AddImageBottomSheetLayoutBinding.bind(view)
        bottomSheetBinding.buttonCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetBinding.buttonAdd.setOnClickListener {
            val imageUrl = bottomSheetBinding.urlInputEditText.text.toString()
            val imageCaption = bottomSheetBinding.captionInputEditText.text.toString()
            if (!Patterns.WEB_URL.matcher(imageUrl).matches()) {
                bottomSheetBinding.urlInputLayout.error = getString(R.string.invalid_url)
            } else if (imageCaption.isEmpty()) {
                bottomSheetBinding.urlInputLayout.error = ""
                bottomSheetBinding.captionInputLayout.error =
                    getString(R.string.caption_cannot_be_blank)
            } else {
                sharedViewModel.postPlayerImage(player.id, imageUrl, imageCaption)
                bottomSheetBinding.urlInputEditText.text?.clear()
                bottomSheetBinding.urlInputLayout.error = ""
                bottomSheetBinding.urlInputLayout.clearFocus()
                bottomSheetBinding.captionInputEditText.text?.clear()
                bottomSheetBinding.captionInputLayout.error = ""
                bottomSheetBinding.captionInputLayout.clearFocus()
                bottomSheetDialog.dismiss()
            }
        }
        bottomSheetDialog.setContentView(view)
        binding.playerDetailsCard.iconAddPhoto.setOnClickListener {
            bottomSheetDialog.show()
        }
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
        binding.playerDetailsCard.teamLogo.backgroundCard.setCardBackgroundColor(
            ContextCompat.getColorStateList(
                requireContext(),
                colorId
            )
        )
    }
}