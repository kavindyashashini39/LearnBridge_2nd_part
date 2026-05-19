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
import com.app.learnbridge.databinding.FragmentSubscriptionBinding
import com.app.learnbridge.util.SessionManager
import com.app.learnbridge.viewmodel.SubscriptionViewModel

class SubscriptionFragment : Fragment() {
    private var _binding: FragmentSubscriptionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SubscriptionViewModel by viewModels {
        SubscriptionViewModel.SubscriptionViewModelFactory((requireActivity().application as LearnBridgeApplication).subscriptionRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubscriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = SessionManager(requireContext()).getUserId()

        viewModel.getLatestSubscription(userId).observe(viewLifecycleOwner) { subscription ->
            if (subscription?.plan == "Premium") {
                binding.btnFree.text = "Switch to Free"
                binding.btnFree.isEnabled = true
                binding.btnPremium.text = "Current Plan"
                binding.btnPremium.isEnabled = false
            } else {
                binding.btnFree.text = "Current Plan"
                binding.btnFree.isEnabled = false
                binding.btnPremium.text = "Upgrade Now"
                binding.btnPremium.isEnabled = true
            }
        }

        binding.btnPremium.setOnClickListener {
            viewModel.upgradeToPremium(userId)
            Toast.makeText(requireContext(), "Upgraded to Premium!", Toast.LENGTH_SHORT).show()
        }

        binding.btnFree.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Cancel Subscription")
                .setMessage("Are you sure you want to cancel your Premium subscription?")
                .setPositiveButton("Yes, Cancel") { _, _ ->
                    viewModel.cancelSubscription(userId)
                    Toast.makeText(requireContext(), "Subscription cancelled", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Keep Premium", null)
                .show()
        }

        binding.btnViewHistory.setOnClickListener {
            findNavController().navigate(R.id.action_subscriptionFragment_to_transactionHistoryFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
