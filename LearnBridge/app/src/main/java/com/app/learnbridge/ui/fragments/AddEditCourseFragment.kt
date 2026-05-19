package com.app.learnbridge.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.app.learnbridge.LearnBridgeApplication
import com.app.learnbridge.databinding.FragmentAddEditCourseBinding
import com.app.learnbridge.db.Course
import com.app.learnbridge.viewmodel.CourseViewModel

class AddEditCourseFragment : Fragment() {

    private var _binding: FragmentAddEditCourseBinding? = null
    private val binding get() = _binding!!

    private val args: AddEditCourseFragmentArgs by navArgs()
    private val courseViewModel: CourseViewModel by viewModels {
        CourseViewModel.CourseViewModelFactory((requireActivity().application as LearnBridgeApplication).courseRepository)
    }

    private var existingCourse: Course? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val courseId = args.courseId
        if (courseId != -1) {
            binding.tvTitle.text = "Edit Course"
            courseViewModel.getCourseById(courseId).observe(viewLifecycleOwner) { course ->
                course?.let {
                    existingCourse = it
                    binding.etCourseTitle.setText(it.title)
                    binding.etCategory.setText(it.category)
                    binding.etLevel.setText(it.level)
                    binding.etDescription.setText(it.description)
                    binding.etImageUrl.setText(it.imageUrl)
                    binding.cbIsPremium.isChecked = it.isPremium
                }
            }
        } else {
            binding.tvTitle.text = "Add New Course"
        }

        binding.btnSave.setOnClickListener {
            saveCourse()
        }
    }

    private fun saveCourse() {
        val title = binding.etCourseTitle.text.toString()
        val category = binding.etCategory.text.toString()
        val level = binding.etLevel.text.toString()
        val description = binding.etDescription.text.toString()
        val imageUrl = binding.etImageUrl.text.toString()
        val isPremium = binding.cbIsPremium.isChecked

        if (title.isEmpty() || category.isEmpty() || level.isEmpty() || description.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val course = if (existingCourse != null) {
            existingCourse!!.copy(
                title = title,
                category = category,
                level = level,
                description = description,
                imageUrl = imageUrl,
                isPremium = isPremium
            )
        } else {
            Course(
                title = title,
                category = category,
                level = level,
                description = description,
                imageUrl = imageUrl,
                isPremium = isPremium
            )
        }

        if (existingCourse != null) {
            courseViewModel.updateCourse(course)
            Toast.makeText(requireContext(), "Course updated", Toast.LENGTH_SHORT).show()
        } else {
            courseViewModel.addCourse(course)
            Toast.makeText(requireContext(), "Course added", Toast.LENGTH_SHORT).show()
        }
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
