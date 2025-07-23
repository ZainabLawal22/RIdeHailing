# RIdeHailing
A mini ride-hailing application built with Kotlin that demonstrates modern Android development practices using MVVM architecture, Google Maps integration, and comprehensive testing.

Core Features

Google Maps Integration: Interactive map for location selection
Fare Calculation System: Dynamic pricing based on distance, demand, and traffic
Location Selection: Pickup and destination selection via map interaction
Ride Request Flow: Complete flow from fare estimate to ride confirmation
Ride History: Local storage and display of past rides

Technical Implementation

MVVM Architecture: Clean separation of concerns
Dependency Injection: Hilt for dependency management(to be completed)
Local Database: Room for ride history persistence
Mocked Backend: Simulated API responses for realistic testing
Navigation: Jetpack Navigation with bottom navigation
Coroutines: Asynchronous operations and background tasks

Testing Coverage

Unit Tests: Use cases, ViewModels, utilities
Instrumented Tests: Database operations and UI interactions
Test Scenarios: All required test cases from assessment document
