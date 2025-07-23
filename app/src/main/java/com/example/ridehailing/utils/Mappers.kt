package com.example.ridehailing.utils


import com.example.ridehailing.data.api.models.DriverResponse
import com.example.ridehailing.data.api.models.FareEstimateResponse
import com.example.ridehailing.data.database.entities.EntityRide
import com.example.ridehailing.domain.model.*

// Entity to Domain Mappers
fun EntityRide.toDomain(): Ride {
    return Ride(
        id = id,
        pickupLocation = Location(pickupLatitude, pickupLongitude, pickupAddress),
        destinationLocation = Location(destinationLatitude, destinationLongitude, destinationAddress),
        fareEstimate = FareCalculation(
            baseFare = baseFare,
            distanceFare = distanceFare,
            demandMultiplier = demandMultiplier,
            trafficMultiplier = trafficMultiplier,
            totalFare = totalFare,
            distance = distance,
            estimatedDuration = estimatedDuration
        ),
        driver = if (driverName != null) {
            Driver(
                name = driverName,
                car = driverCar ?: "",
                plateNumber = driverPlateNumber ?: "",
                rating = driverRating ?: 0f,
                estimatedArrival = estimatedArrival ?: ""
            )
        } else null,
        status = status,
        requestTime = requestTime,
        completionTime = completionTime
    )
}

// Domain to Entity Mappers
fun Ride.toEntity(): EntityRide {
    return EntityRide(
        id = id,
        pickupLatitude = pickupLocation.latitude,
        pickupLongitude = pickupLocation.longitude,
        pickupAddress = pickupLocation.address,
        destinationLatitude = destinationLocation.latitude,
        destinationLongitude = destinationLocation.longitude,
        destinationAddress = destinationLocation.address,
        baseFare = fareEstimate.baseFare,
        distanceFare = fareEstimate.distanceFare,
        demandMultiplier = fareEstimate.demandMultiplier,
        trafficMultiplier = fareEstimate.trafficMultiplier,
        totalFare = fareEstimate.totalFare,
        distance = fareEstimate.distance,
        estimatedDuration = fareEstimate.estimatedDuration,
        driverName = driver?.name,
        driverCar = driver?.car,
        driverPlateNumber = driver?.plateNumber,
        driverRating = driver?.rating,
        estimatedArrival = driver?.estimatedArrival,
        status = status,
        requestTime = requestTime,
        completionTime = completionTime
    )
}

// API to Domain Mappers
fun FareEstimateResponse.toDomain(): FareCalculation {
    return FareCalculation(
        baseFare = baseFare,
        distanceFare = distanceFare,
        demandMultiplier = demandMultiplier,
        trafficMultiplier = trafficMultiplier,
        totalFare = totalFare,
        distance = distance,
        estimatedDuration = estimatedDuration
    )
}

fun DriverResponse.toDomain(): Driver {
    return Driver(
        name = name,
        car = car,
        plateNumber = plateNumber,
        rating = rating
    )
}

// List mappers
fun List<EntityRide>.toDomain(): List<Ride> = map { it.toDomain() }
