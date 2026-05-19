package com.app.learnbridge.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.app.learnbridge.LearnBridgeApplication
import com.app.learnbridge.databinding.FragmentCoursePlayerBinding
import com.app.learnbridge.db.Enrollment
import com.app.learnbridge.util.SessionManager
import com.app.learnbridge.viewmodel.CourseViewModel

class CoursePlayerFragment : Fragment() {
    private var _binding: FragmentCoursePlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CourseViewModel by viewModels {
        CourseViewModel.CourseViewModelFactory((requireActivity().application as LearnBridgeApplication).courseRepository)
    }

    private val userViewModel: com.app.learnbridge.viewmodel.UserViewModel by viewModels {
        com.app.learnbridge.viewmodel.UserViewModel.UserViewModelFactory((requireActivity().application as LearnBridgeApplication).userRepository)
    }

    private var currentEnrollment: Enrollment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoursePlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val courseId = arguments?.getInt("courseId") ?: -1
        val userId = SessionManager(requireContext()).getUserId()

        if (courseId != -1) {
            viewModel.getCourseById(courseId).observe(viewLifecycleOwner) { course ->
                binding.tvPlayerCourseTitle.text = course?.title
            }

            viewModel.getEnrollment(userId, courseId).observe(viewLifecycleOwner) { enrollment ->
                currentEnrollment = enrollment
                updateProgressUI(enrollment?.progress ?: 0)
            }
        }

        binding.btnMarkComplete.setOnClickListener {
            currentEnrollment?.let { enrollment ->
                val newProgress = (enrollment.progress + 10).coerceAtMost(100)
                viewModel.updateProgress(userId, courseId, newProgress)
                
                // Gamification: Reward XP
                if (newProgress > enrollment.progress) {
                    userViewModel.addXp(userId, 10)
                    Toast.makeText(requireContext(), "Lesson Complete! +10 XP", Toast.LENGTH_SHORT).show()
                }

                if (newProgress == 100 && enrollment.progress < 100) {
                    userViewModel.addXp(userId, 200)
                    Toast.makeText(requireContext(), "Course Completed! +200 XP", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateProgressUI(progress: Int) {
        binding.pbPlayerProgress.progress = progress
        binding.tvPlayerProgressPercent.text = "$progress% complete"
        
        if (progress == 100) {
            binding.btnMarkComplete.visibility = View.GONE
            binding.tvCompletionStatus.visibility = View.VISIBLE
        } else {
            binding.btnMarkComplete.visibility = View.VISIBLE
            binding.tvCompletionStatus.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
