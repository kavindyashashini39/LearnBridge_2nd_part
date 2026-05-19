package com.app.learnbridge.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.learnbridge.LearnBridgeApplication
import com.app.learnbridge.R
import com.app.learnbridge.databinding.FragmentProfileBinding
import com.app.learnbridge.util.SessionManager
import com.app.learnbridge.viewmodel.UserViewModel

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory((requireActivity().application as LearnBridgeApplication).userRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()

        userViewModel.getUserById(userId).observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvProfileName.text = it.name
                binding.tvProfileEmail.text = it.email
            }
        }

        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.editProfileFragment)
        }

        binding.llSubscription.setOnClickListener {
            findNavController().navigate(R.id.subscriptionFragment)
        }

        binding.llSettings.setOnClickListener {
            findNavController().navigate(R.id.changePasswordFragment)
        }

        binding.btnDeleteAccount.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton("Delete") { _, _ ->
                    userViewModel.getUserById(userId).observe(viewLifecycleOwner) { user ->
                        user?.let {
                            userViewModel.deleteUser(it)
                            sessionManager.logout()
                            findNavController().navigate(R.id.authFragment)
                            Toast.makeText(requireContext(), "Account deleted", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.llLogout.setOnClickListener {
            sessionManager.logout()
            findNavController().navigate(R.id.authFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
