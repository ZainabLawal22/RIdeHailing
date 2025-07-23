package com.example.ridehailing.presentation.ui.home

import androidx.fragment.app.Fragment
import com.example.ridehailing.R
import com.example.ridehailing.databinding.FragmentHomeBinding
import com.example.ridehailing.domain.model.Location
import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale
import android.util.Log

import android.widget.Toast


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

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            enableMyLocation()
        } else {
            Toast.makeText(requireContext(), "Location permission needed to show your current location", Toast.LENGTH_LONG).show()

            val lagos = LatLng(6.5244, 3.3792)
            if (::googleMap.isInitialized) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lagos, 12f))
            }
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

        // Initially select pickup
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
                binding.tvBaseFare.text = "₦${String.format("%.0f", fareEstimate.baseFare)}"
                binding.tvDistanceFare.text = "₦${String.format("%.0f", fareEstimate.distanceFare)}"
                binding.tvDemandMultiplier.text = String.format("%.1fx", fareEstimate.demandMultiplier)
                binding.tvTrafficMultiplier.text = String.format("%.1fx", fareEstimate.trafficMultiplier)
                binding.tvTotalFare.text = "₦${String.format("%.0f", fareEstimate.totalFare)}"
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
                "Lat: ${String.format("%.4f", location.latitude)}, Lng: ${String.format("%.4f", location.longitude)}"
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
                "Lat: ${String.format("%.4f", location.latitude)}, Lng: ${String.format("%.4f", location.longitude)}"
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val lagos = LatLng(6.5244, 3.3792)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lagos, 12f))

        // Style the map
        styleMap()

        setupMapClickListener()
        requestLocationPermission()
    }

    private fun styleMap() {
        try {
            // Enable various map features
            googleMap.uiSettings.apply {
                isZoomControlsEnabled = true
                isCompassEnabled = true
                isMyLocationButtonEnabled = true
                isMapToolbarEnabled = true
                isRotateGesturesEnabled = true
                isScrollGesturesEnabled = true
                isTiltGesturesEnabled = true
                isZoomGesturesEnabled = true
            }

            // Set map type to normal (you can change to satellite, hybrid, etc.)
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            Log.d("RideApp", "Map styled successfully")

        } catch (e: Exception) {
            Log.e("RideApp", "Error styling map", e)
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
                val address = addresses[0]
                // Build a readable address for Nigerian locations
                buildString {
                    address.thoroughfare?.let { append("$it, ") }
                    address.locality?.let { append("$it, ") }
                    address.adminArea?.let { append("$it, ") }
                    address.countryName?.let { append(it) }
                }.trim().removeSuffix(",")
            } else {
                "Unknown Location"
            }
        } catch (e: Exception) {
            Log.e("RideApp", "Error getting address", e)
            "Location: ${String.format("%.4f", latitude)}, ${String.format("%.4f", longitude)}"
        }
    }

    private fun requestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                enableMyLocation()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Toast.makeText(requireContext(), "Location permission needed to show your current location", Toast.LENGTH_LONG).show()
                requestLocationPermissions()
            }

            else -> {
                requestLocationPermissions()
            }
        }
    }

    private fun requestLocationPermissions() {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true

            // Get current location and move camera to user's location
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    Log.d("RideApp", "Current location: ${it.latitude}, ${it.longitude}")
                    Toast.makeText(requireContext(), "Showing your current location", Toast.LENGTH_SHORT).show()
                } ?: run {
                    // If no location available, default to Lagos
                    val lagos = LatLng(6.5244, 3.3792)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lagos, 12f))
                    Toast.makeText(requireContext(), "Using default location (Lagos). Enable GPS for accurate location.", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener {
                // Fallback to Lagos if location fails
                val lagos = LatLng(6.5244, 3.3792)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lagos, 12f))
                Toast.makeText(requireContext(), "Could not get current location. Using Lagos as default.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}