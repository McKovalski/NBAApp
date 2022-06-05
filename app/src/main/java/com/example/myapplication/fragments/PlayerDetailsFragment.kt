package com.example.myapplication.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.activities.PlayerDetailsActivity
import com.example.myapplication.adapters.ImagePagerAdapter
import com.example.myapplication.adapters.PhotosRecyclerAdapter
import com.example.myapplication.databinding.AddImageBottomSheetLayoutBinding
import com.example.myapplication.databinding.AlertDialogViewBinding
import com.example.myapplication.databinding.FragmentPlayerDetailsBinding
import com.example.myapplication.databinding.ManagePhotosBottomSheetLayoutBinding
import com.example.myapplication.helpers.TeamsHelper
import com.example.myapplication.models.Player
import com.example.myapplication.models.PlayerImage
import com.example.myapplication.viewmodels.SharedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar

class PlayerDetailsFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var _binding: FragmentPlayerDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val photosRecyclerAdapter by lazy {
        PhotosRecyclerAdapter(
            requireContext(),
            mutableListOf(),
            this
        )
    }

    private lateinit var player: Player
    private val images = mutableListOf<PlayerImage>()
    private var favouriteImage: PlayerImage? = null
    private var isEditing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerDetailsBinding.inflate(inflater, container, false)

        player = (activity as PlayerDetailsActivity).player

        val imagePagerAdapter = ImagePagerAdapter(requireContext(), mutableListOf())
        binding.playerDetailsCard.viewPager.adapter = imagePagerAdapter
        binding.playerDetailsCard.circleIndicator.setViewPager(binding.playerDetailsCard.viewPager)

        binding.playerDetailsCard.iconEdit.visibility = View.GONE

        sharedViewModel.getPlayerImages(player.id)
        sharedViewModel.getPlayerFavouriteImage(requireContext(), player.id)
        sharedViewModel.playerImages.observe(viewLifecycleOwner) {
            images.addAll(it.filter { image -> image.playerId == player.id && !images.contains(image) })
            photosRecyclerAdapter.updatePhotos(images)
            Log.d("images", images.toString())
            imagePagerAdapter.updateImages(images)
            sharedViewModel.playerFavouriteImage.observe(viewLifecycleOwner) { favImage ->
                if (favImage != null) {
                    favouriteImage = favImage
                    binding.playerDetailsCard.iconEdit.visibility = View.VISIBLE
                } else {
                    if (images.isNotEmpty()) {
                        favouriteImage = images[0]
                        sharedViewModel.setPlayerFavouriteImage(requireContext(), favouriteImage!!)
                        binding.playerDetailsCard.iconEdit.visibility = View.VISIBLE
                    } else {
                        binding.playerDetailsCard.iconEdit.visibility = View.GONE
                    }
                }
                photosRecyclerAdapter.setFavouritePhoto(favouriteImage!!)
                val favouriteImagePosition = images.indexOf(favouriteImage)
                binding.playerDetailsCard.viewPager.adapter = imagePagerAdapter
                if (favouriteImagePosition >= 0) {
                    binding.playerDetailsCard.viewPager.currentItem = favouriteImagePosition
                } else {
                    binding.playerDetailsCard.viewPager.currentItem = 0
                }
                binding.playerDetailsCard.circleIndicator.setViewPager(binding.playerDetailsCard.viewPager)
            }
        }

        binding.playerDetailsCard.iconEdit.setOnClickListener {
            showManagePhotosBottomSheet()
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

    private fun showManagePhotosBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.manage_photos_bottom_sheet_layout, null)
        val bottomSheetBinding = ManagePhotosBottomSheetLayoutBinding.bind(view)

        // Set isEditing variable to false at the start and adjust views
        if (isEditing) {
            photosRecyclerAdapter.switchEdit()
        }
        isEditing = false
        bottomSheetBinding.buttonDeleteAllPhotos.isVisible = false

        bottomSheetBinding.recycler.adapter = photosRecyclerAdapter
        bottomSheetBinding.recycler.layoutManager = LinearLayoutManager(requireContext())

        bottomSheetBinding.header.iconEdit.setOnClickListener {
            if (!isEditing) {
                isEditing = true
                bottomSheetBinding.header.iconEdit.setImageResource(R.drawable.ic_close)
                bottomSheetBinding.buttonDeleteAllPhotos.isVisible = true
            } else {
                isEditing = false
                bottomSheetBinding.header.iconEdit.setImageResource(R.drawable.ic_edit)
                bottomSheetBinding.buttonDeleteAllPhotos.isVisible = false
            }
            photosRecyclerAdapter.switchEdit()
        }

        bottomSheetBinding.buttonDeleteAllPhotos.setOnClickListener {
            // Show the alert dialog
            val alertView =
                LayoutInflater.from(requireContext()).inflate(R.layout.alert_dialog_view, null)
            val alertBinding = AlertDialogViewBinding.bind(alertView)
            val clearRecentAlertDialog = AlertDialog.Builder(requireContext())
                .setView(alertView)
                .create()
            clearRecentAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            clearRecentAlertDialog.show()
            alertBinding.title.text = getString(R.string.delete_all_player_photos)
            alertBinding.textBody.text = getString(R.string.delete_player_photos_alert_text)
            alertBinding.buttonCancel.setOnClickListener {
                clearRecentAlertDialog.dismiss()
            }
            alertBinding.buttonClear.setOnClickListener {
                for (image in images) {
                    sharedViewModel.deletePlayerImage(image)
                }
                images.clear()
                photosRecyclerAdapter.updatePhotos(images)
                sharedViewModel.deletePlayerFavouriteImage(requireContext(), favouriteImage!!)
                showRemovePhotosSnackbar(getString(R.string.deleted_all_player_photos))
                clearRecentAlertDialog.dismiss()
                bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun showRemovePhotosSnackbar(title: String) {
        val snackbar = Snackbar.make(
            requireView(),
            title,
            Snackbar.LENGTH_LONG
        )
        val textView =
            snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close_white, 0)
        //snackbar.setAnchorView(R.id.bottomNavigationView)
        snackbar.view.setPadding(16, 18, 16, 18)
        snackbar.view.setBackgroundResource(R.drawable.snackbar_background)
        textView.setOnClickListener {
            snackbar.dismiss()
        }
        snackbar.show()
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

    fun setFavouritePhoto(photo: PlayerImage) {
        sharedViewModel.setPlayerFavouriteImage(requireContext(), photo)
        sharedViewModel.getPlayerFavouriteImage(requireContext(), player.id)
        photosRecyclerAdapter.setFavouritePhoto(photo)
    }

    fun deletePlayerPhoto(photo: PlayerImage) {
        sharedViewModel.deletePlayerImage(photo)
        images.remove(photo)
        // If we removed the photo that was favourite, set the first photo in list as favourite
        if (photo == favouriteImage) {
            sharedViewModel.deletePlayerFavouriteImage(requireContext(), photo)
            if (images.isNotEmpty()) {
                favouriteImage = images[0]
                sharedViewModel.setPlayerFavouriteImage(requireContext(), favouriteImage!!)
                photosRecyclerAdapter.setFavouritePhoto(favouriteImage!!)
            }
        }
        photosRecyclerAdapter.updatePhotos(images)
    }
}