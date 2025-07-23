package com.example.ridehailing.presentation.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.example.ridehailing.R
import com.example.ridehailing.databinding.FragmentHomeBinding
import com.example.ridehailing.domain.model.Location
import java.util.Locale

class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder

    private var pickupMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private var isSelectingPickup = true

    private val TAG = HomeFragment::class.java.simpleName

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            enableMyLocation()
        } else {
            Toast.makeText(requireContext(), "Location permission needed to show your current location", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMap()
        setupUI()
        observeViewModel()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geocoder = Geocoder(requireContext(), Locale.getDefault())
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupUI() {
        binding.btnGetFareEstimate.setOnClickListener {
            viewModel.getFareEstimate()
        }

        binding.btnRequestRide.setOnClickListener {
            viewModel.requestRide()
        }

        binding.btnSelectPickup.setOnClickListener {
            isSelectingPickup = true
            binding.btnSelectPickup.isSelected = true
            binding.btnSelectDestination.isSelected = false
            Toast.makeText(requireContext(), "Tap on map to select pickup location", Toast.LENGTH_SHORT).show()
        }

        binding.btnSelectDestination.setOnClickListener {
            isSelectingPickup = false
            binding.btnSelectPickup.isSelected = false
            binding.btnSelectDestination.isSelected = true
            Toast.makeText(requireContext(), "Tap on map to select destination", Toast.LENGTH_SHORT).show()
        }


        binding.btnSelectPickup.isSelected = true
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            updateUIState(state)
        }

        viewModel.pickupLocation.observe(viewLifecycleOwner) { location ->
            updatePickupLocation(location)
        }

        viewModel.destinationLocation.observe(viewLifecycleOwner) { location ->
            updateDestinationLocation(location)
        }

        viewModel.fareEstimate.observe(viewLifecycleOwner) { fareEstimate ->
            if (fareEstimate != null) {
                binding.fareEstimateCard.visibility = View.VISIBLE
                binding.tvBaseFare.text = "₦${String.format("%.2f", fareEstimate.baseFare)}"
                binding.tvDistanceFare.text = "₦${String.format("%.2f", fareEstimate.distanceFare)}"
                binding.tvDemandMultiplier.text = String.format("%.1fx", fareEstimate.demandMultiplier)
                binding.tvTrafficMultiplier.text = String.format("%.1fx", fareEstimate.trafficMultiplier)
                binding.tvTotalFare.text = "₦${String.format("%.2f", fareEstimate.totalFare)}"
                binding.tvDistance.text = String.format("%.1f km", fareEstimate.distance)
                binding.tvEstimatedDuration.text = "${fareEstimate.estimatedDuration} min"
            } else {
                binding.fareEstimateCard.visibility = View.GONE
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearErrorMessage()
            }
        }

        viewModel.successMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearSuccessMessage()
            }
        }
    }

    private fun updateUIState(state: HomeUiState) {
        when (state) {
            HomeUiState.INITIAL -> {
                binding.progressBar.visibility = View.GONE
                binding.btnGetFareEstimate.isEnabled = false
                binding.btnRequestRide.isEnabled = false
            }

            HomeUiState.PICKUP_SET -> {
                binding.progressBar.visibility = View.GONE
                binding.btnGetFareEstimate.isEnabled = false
                binding.btnRequestRide.isEnabled = false
            }

            HomeUiState.LOCATIONS_SET -> {
                binding.progressBar.visibility = View.GONE
                binding.btnGetFareEstimate.isEnabled = true
                binding.btnRequestRide.isEnabled = false
            }

            HomeUiState.LOADING_FARE -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnGetFareEstimate.isEnabled = false
                binding.btnRequestRide.isEnabled = false
            }

            HomeUiState.FARE_LOADED -> {
                binding.progressBar.visibility = View.GONE
                binding.btnGetFareEstimate.isEnabled = true
                binding.btnRequestRide.isEnabled = true
            }

            HomeUiState.REQUESTING_RIDE -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnGetFareEstimate.isEnabled = false
                binding.btnRequestRide.isEnabled = false
            }

            HomeUiState.RIDE_REQUESTED -> {
                binding.progressBar.visibility = View.GONE
                binding.btnGetFareEstimate.isEnabled = false
                binding.btnRequestRide.isEnabled = false
            }

            HomeUiState.ERROR -> {
                binding.progressBar.visibility = View.GONE
                binding.btnGetFareEstimate.isEnabled = viewModel.canRequestFare()
                binding.btnRequestRide.isEnabled = viewModel.canRequestRide()
            }
        }
    }

    private fun updatePickupLocation(location: Location) {
        if (::googleMap.isInitialized && location.isValid()) {
            val latLng = LatLng(location.latitude, location.longitude)
            pickupMarker?.remove()
            pickupMarker = googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Pickup Location")
                    .snippet(location.address.ifEmpty { "Selected Location" })
            )
            binding.tvPickupLocation.text = location.address.ifEmpty {
                "${location.latitude}, ${location.longitude}"
            }
        }
    }

    private fun updateDestinationLocation(location: Location) {
        if (::googleMap.isInitialized && location.isValid()) {
            val latLng = LatLng(location.latitude, location.longitude)
            destinationMarker?.remove()
            destinationMarker = googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Destination")
                    .snippet(location.address.ifEmpty { "Selected Location" })
            )
            binding.tvDestinationLocation.text = location.address.ifEmpty {
                "${location.latitude}, ${location.longitude}"
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map


        val defaultLocation = LatLng(6.659417, 3.344079)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f))


        setMapStyle(googleMap)
        setupMapClickListener()
        enableMyLocation()


        googleMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMyLocationButtonEnabled = true
        }
    }

    private fun setupMapClickListener() {
        googleMap.setOnMapClickListener { latLng ->
            val address = getAddressFromLocation(latLng.latitude, latLng.longitude)
            val location = Location(latLng.latitude, latLng.longitude, address)

            if (isSelectingPickup) {
                viewModel.setPickupLocation(location)
                // Auto-switch to destination selection
                binding.btnSelectPickup.isSelected = false
                binding.btnSelectDestination.isSelected = true
                isSelectingPickup = false
                Toast.makeText(requireContext(), "Pickup set! Now tap to select destination", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.setDestinationLocation(location)
                Toast.makeText(requireContext(), "Destination set! You can now get fare estimate", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double): String {
        return try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses?.isNotEmpty() == true) {
                addresses[0].getAddressLine(0) ?: ""
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }


    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            googleMap.isMyLocationEnabled = true
            // Move camera to user's current location
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }


    private fun setMapStyle(map: GoogleMap) {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}