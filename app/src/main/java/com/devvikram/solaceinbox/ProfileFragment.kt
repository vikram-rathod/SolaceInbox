package com.devvikram.solaceinbox

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.devvikram.solaceinbox.activities.login.AuthViewModel
import com.devvikram.solaceinbox.activities.login.LoginActivity
import com.devvikram.solaceinbox.constant.MyApplication
import com.devvikram.solaceinbox.constant.SharedPreference
import com.devvikram.solaceinbox.databinding.FragmentProfileBinding
import com.devvikram.solaceinbox.databinding.ItemEmailLayoutBinding
import com.devvikram.solaceinbox.model.Mail

class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private lateinit var sharedPreference: SharedPreference
    private val authViewModel: AuthViewModel by lazy {
        (requireActivity().application as MyApplication).authViewModel
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreference = SharedPreference(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.logoutButton.setOnClickListener {
            showLogout()
        }
        binding.userName.text = sharedPreference.getUserName()
        binding.userEmail.text = sharedPreference.getUserEmail()
        loadProfileImage()
    }
    private fun showLogout() {
        val alertDialog = AlertDialog.Builder(requireActivity()).create()
        alertDialog.setTitle("Logout")
        alertDialog.setMessage("Are you sure you want to logout?")

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes") { _, _ ->
            authViewModel.logoutUser()
            navigateToLogin()
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()

    }


    private fun navigateToLogin() {
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finishAffinity()
    }

    private fun loadProfileImage() {
        val profileUrl = ""
        if (profileUrl.isEmpty()) {
            binding.profileImage.visibility = View.GONE
            binding.noProfileLayout.visibility = View.VISIBLE
            binding.firstLetterTextView.text = sharedPreference.getUserName().firstOrNull()?.toString() ?: "?"
            binding.subCatIcon.setBackgroundColor(
                ContextCompat.getColor(requireActivity(), R.color.purple_light_2)
            )
        } else {
            binding.profileImage.visibility = View.VISIBLE
            binding.noProfileLayout.visibility = View.GONE
            Glide.with(requireActivity())
                .load(profileUrl)
                .placeholder(R.drawable.baseline_person_24)
                .into(binding.profileImage)
        }
    }
    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
            }
    }
}