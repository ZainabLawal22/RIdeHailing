package com.example.ridehailing.presentation.adapter

import android.os.Build
import com.example.ridehailing.domain.model.Ride
import com.example.ridehailing.domain.model.RideStatus
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ridehailing.R
import com.example.ridehailing.databinding.ItemRideHistoryBinding
import com.example.ridehailing.utils.formatAsCurrency
import com.example.ridehailing.utils.formatAsDistance
import com.example.ridehailing.utils.formatForDisplay


class RideHistoryAdapter : ListAdapter<Ride, RideHistoryAdapter.RideHistoryViewHolder>(RideDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideHistoryViewHolder {
        val binding = ItemRideHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RideHistoryViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RideHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RideHistoryViewHolder(
        private val binding: ItemRideHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(ride: Ride) {
            binding.apply {

                tvPickupLocation.text = ride.pickupLocation.address.ifEmpty {
                    "${ride.pickupLocation.latitude}, ${ride.pickupLocation.longitude}"
                }
                tvDestinationLocation.text = ride.destinationLocation.address.ifEmpty {
                    "${ride.destinationLocation.latitude}, ${ride.destinationLocation.longitude}"
                }


                tvTotalFare.text = ride.fareEstimate.totalFare.formatAsCurrency()
                tvDistance.text = ride.fareEstimate.distance.formatAsDistance()
                tvDuration.text = "${ride.fareEstimate.estimatedDuration} min"


                tvRideDate.text = ride.requestTime.formatForDisplay()


                if (ride.driver != null) {
                    tvDriverName.text = ride.driver.name
                    tvDriverCar.text = "${ride.driver.car} - ${ride.driver.plateNumber}"
                    if (ride.driver.rating > 0) {
                        tvDriverRating.text = String.format("%.1f ⭐", ride.driver.rating)
                    } else {
                        tvDriverRating.text = "N/A"
                    }
                } else {
                    tvDriverName.text = "No driver assigned"
                    tvDriverCar.text = ""
                    tvDriverRating.text = ""
                }


                tvRideStatus.text = formatRideStatus(ride.status)
                tvRideStatus.setTextColor(getStatusColor(ride.status))


                ivStatusIcon.setColorFilter(getStatusColor(ride.status))
            }
        }

        private fun formatRideStatus(status: RideStatus): String {
            return when (status) {
                RideStatus.REQUESTED -> "Requested"
                RideStatus.CONFIRMED -> "Confirmed"
                RideStatus.DRIVER_ASSIGNED -> "Driver Assigned"
                RideStatus.IN_PROGRESS -> "In Progress"
                RideStatus.COMPLETED -> "Completed"
                RideStatus.CANCELLED -> "Cancelled"
            }
        }

        private fun getStatusColor(status: RideStatus): Int {
            val context = binding.root.context
            return when (status) {
                RideStatus.REQUESTED -> ContextCompat.getColor(context, R.color.status_requested)
                RideStatus.CONFIRMED -> ContextCompat.getColor(context, R.color.status_confirmed)
                RideStatus.DRIVER_ASSIGNED -> ContextCompat.getColor(context, R.color.status_driver_assigned)
                RideStatus.IN_PROGRESS -> ContextCompat.getColor(context, R.color.status_in_progress)
                RideStatus.COMPLETED -> ContextCompat.getColor(context, R.color.status_completed)
                RideStatus.CANCELLED -> ContextCompat.getColor(context, R.color.status_cancelled)
            }
        }
    }

    private class RideDiffCallback : DiffUtil.ItemCallback<Ride>() {
        override fun areItemsTheSame(oldItem: Ride, newItem: Ride): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Ride, newItem: Ride): Boolean {
            return oldItem == newItem
        }
    }
}