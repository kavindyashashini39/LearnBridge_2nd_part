package com.app.learnbridge.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.app.learnbridge.LearnBridgeApplication
import com.app.learnbridge.databinding.FragmentEditProfileBinding
import com.app.learnbridge.db.User
import com.app.learnbridge.util.SessionManager
import com.app.learnbridge.viewmodel.UserViewModel

class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory((requireActivity().application as LearnBridgeApplication).userRepository)
    }

    private var currentUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val userId = SessionManager(requireContext()).getUserId()

        userViewModel.getUserById(userId).observe(viewLifecycleOwner) { user ->
            user?.let {
                currentUser = it
                binding.etEditName.setText(it.name)
                binding.etEditEmail.setText(it.email)
            }
        }

        binding.btnSaveProfile.setOnClickListener {
            val name = binding.etEditName.text.toString()
            val email = binding.etEditEmail.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty()) {
                currentUser?.let {
                    val updatedUser = it.copy(name = name, email = email)
                    userViewModel.updateUser(updatedUser)
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
