package com.example.myapplication.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.example.myapplication.R
import com.example.myapplication.activities.AboutActivity
import com.example.myapplication.databinding.AlertDialogViewBinding
import com.example.myapplication.databinding.FragmentSettingsBinding
import com.example.myapplication.viewmodels.SharedViewModel
import com.google.android.material.snackbar.Snackbar

class SettingsFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.aboutCard.moreInfoButton.setOnClickListener {
            val intent = Intent(requireContext(), AboutActivity::class.java)
            requireContext().startActivity(intent)
        }

        binding.clearMyFavouritesButton.setOnClickListener {
            val alertBinding = AlertDialogViewBinding.inflate(inflater)
            val clearRecentAlertDialog = AlertDialog.Builder(requireContext())
                .setView(alertBinding.root)
                .create()
            clearRecentAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            clearRecentAlertDialog.show()

            alertBinding.title.text = getString(R.string.clear_favourites_list_question)
            alertBinding.textBody.text = getString(R.string.clear_favourites_body)
            alertBinding.buttonClear.setOnClickListener {
                sharedViewModel.removeAllFavouriteTeams(requireContext())
                sharedViewModel.removeAllFavouritePlayers(requireContext())
                clearRecentAlertDialog.dismiss()
                showRemovedFavouritesSnackbar(getString(R.string.favourites_list_is_clear))
            }
            alertBinding.buttonCancel.setOnClickListener {
                clearRecentAlertDialog.dismiss()
            }
        }

        return binding.root
    }

    private fun showRemovedFavouritesSnackbar(title: String) {
        val snackbar = Snackbar.make(
            requireView(),
            title,
            Snackbar.LENGTH_LONG
        )
        val textView =
            snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close_white, 0)
        snackbar.setAnchorView(R.id.bottomNavigationView)
        snackbar.view.setPadding(16, 18, 16, 18)
        snackbar.view.setBackgroundResource(R.drawable.snackbar_background)
        textView.setOnClickListener {
            snackbar.dismiss()
        }
        snackbar.show()
    }
}