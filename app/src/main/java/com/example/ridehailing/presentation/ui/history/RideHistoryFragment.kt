package com.example.ridehailing.presentation.ui.history

import androidx.fragment.app.Fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ridehailing.databinding.FragmentRideHistoryBinding
import com.example.ridehailing.presentation.adapter.RideHistoryAdapter
import com.example.ridehailing.utils.gone
import com.example.ridehailing.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RideHistoryFragment : Fragment() {

    private var _binding: FragmentRideHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RideHistoryViewModel by viewModels()
    private lateinit var rideHistoryAdapter: RideHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRideHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupUI()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        rideHistoryAdapter = RideHistoryAdapter()
        binding.recyclerViewRides.apply {
            adapter = rideHistoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupUI() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshRideHistory()
        }
    }

    private fun observeViewModel() {
        viewModel.rideHistory.observe(viewLifecycleOwner) { rides ->
            rideHistoryAdapter.submitList(rides)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            updateUIState(state)
        }
    }

    private fun updateUIState(state: RideHistoryUiState) {
        when (state) {
            RideHistoryUiState.LOADING -> {
                binding.progressBar.visible()
                binding.recyclerViewRides.gone()
                binding.emptyStateLayout.gone()
            }

            RideHistoryUiState.LOADED -> {
                binding.progressBar.gone()
                binding.recyclerViewRides.visible()
                binding.emptyStateLayout.gone()
            }

            RideHistoryUiState.EMPTY -> {
                binding.progressBar.gone()
                binding.recyclerViewRides.gone()
                binding.emptyStateLayout.visible()
            }

            RideHistoryUiState.ERROR -> {
                binding.progressBar.gone()
                binding.recyclerViewRides.gone()
                binding.emptyStateLayout.visible()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}