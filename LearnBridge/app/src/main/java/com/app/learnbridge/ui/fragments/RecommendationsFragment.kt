package com.app.learnbridge.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.learnbridge.LearnBridgeApplication
import com.app.learnbridge.R
import com.app.learnbridge.databinding.FragmentRecommendationsBinding
import com.app.learnbridge.ui.adapters.CourseAdapter
import com.app.learnbridge.viewmodel.CourseViewModel

class RecommendationsFragment : Fragment() {
    private var _binding: FragmentRecommendationsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CourseViewModel by viewModels {
        CourseViewModel.CourseViewModelFactory((requireActivity().application as LearnBridgeApplication).courseRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val courseAdapter = CourseAdapter { course ->
            val bundle = Bundle().apply {
                putInt("courseId", course.id)
            }
            findNavController().navigate(R.id.courseDetailsFragment, bundle)
        }

        binding.rvRecommendations.apply {
            adapter = courseAdapter
            layoutManager = GridLayoutManager(context, 2)
        }

        viewModel.allCourses.observe(viewLifecycleOwner) { courses ->
            courseAdapter.submitList(courses)
        }

        setupSearch(courseAdapter)
        setupFilters(courseAdapter)
    }

    private fun setupSearch(adapter: CourseAdapter) {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    (requireActivity().application as LearnBridgeApplication).courseRepository
                        .searchCourses(query).asLiveData().observe(viewLifecycleOwner) { courses ->
                            adapter.submitList(courses)
                        }
                } else {
                    viewModel.allCourses.observe(viewLifecycleOwner) { courses ->
                        adapter.submitList(courses)
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupFilters(adapter: CourseAdapter) {
        val filters = mapOf(
            binding.filterAll to "All",
            binding.filterIT to "IT",
            binding.filterHealth to "Health",
            binding.filterTeaching to "Teaching"
        )

        filters.forEach { (view, category) ->
            view.setOnClickListener {
                updateFilterUI(view)
                if (category == "All") {
                    viewModel.allCourses.observe(viewLifecycleOwner) { courses ->
                        adapter.submitList(courses)
                    }
                } else {
                    (requireActivity().application as LearnBridgeApplication).courseRepository
                        .getCoursesByCategory(category).asLiveData().observe(viewLifecycleOwner) { courses ->
                            adapter.submitList(courses)
                        }
                }
            }
        }
    }

    private fun updateFilterUI(selectedView: TextView) {
        val allFilters = listOf(binding.filterAll, binding.filterIT, binding.filterHealth, binding.filterTeaching)
        allFilters.forEach {
            if (it == selectedView) {
                it.setBackgroundResource(R.drawable.bg_survey_item_selected)
            } else {
                it.setBackgroundResource(R.drawable.bg_white_rounded)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
