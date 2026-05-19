package com.app.learnbridge.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.learnbridge.R

import com.app.learnbridge.util.SessionManager

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())
        val llAuthButtons = view.findViewById<View>(R.id.llAuthButtons)
        val ivProfile = view.findViewById<View>(R.id.ivProfile)

        if (sessionManager.isLoggedIn()) {
            llAuthButtons.visibility = View.GONE
            ivProfile.visibility = View.VISIBLE
        } else {
            llAuthButtons.visibility = View.VISIBLE
            ivProfile.visibility = View.GONE
        }

        ivProfile.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }

        view.findViewById<View>(R.id.tvHomeLogin).setOnClickListener {
            findNavController().navigate(R.id.authFragment)
        }
        view.findViewById<View>(R.id.tvHomeSignUp).setOnClickListener {
            findNavController().navigate(R.id.authFragment)
        }
        view.findViewById<View>(R.id.cardCertificates).setOnClickListener {
            findNavController().navigate(R.id.certificatesFragment)
        }
        view.findViewById<View>(R.id.cardBuildCareer).setOnClickListener {
            findNavController().navigate(R.id.recommendationsFragment)
        }
        view.findViewById<View>(R.id.cardEarn).setOnClickListener {
            findNavController().navigate(R.id.earnFragment)
        }
        view.findViewById<View>(R.id.btnRecommendations).setOnClickListener {
            findNavController().navigate(R.id.recommendationsFragment)
        }
    }
}
