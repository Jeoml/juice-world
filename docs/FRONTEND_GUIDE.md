# Juice - Frontend Implementation Guide

This guide covers what needs to be built on the frontend (mobile app) to work with the Juice backend.

---

## Overview

The app helps users discover authentic fruit juice stalls near them. Think of it like Uber, but instead of cars, users see fruit stalls on a map.

---

## Step-by-Step Implementation

### Step 1: Set Up the Project

- **Android:** Create a Kotlin project (Jetpack Compose recommended)
- **iOS:** Create a Swift project (SwiftUI recommended)
- Add dependencies for:
  - HTTP networking (Ktor for Kotlin, URLSession for Swift)
  - Map SDK (Google Maps for Android, MapKit for iOS)
  - Location services
  - Image loading (Coil for Kotlin, SDWebImage/Kingfisher for Swift)

---

### Step 2: Get User's Location

- Request location permissions from the user
- Use the device's GPS to get the user's current latitude and longitude
- Handle the case where the user denies location access (show a message or let them enter location manually)
- Keep updating the location periodically or when the user moves significantly

---

### Step 3: Display a Map

- Show a full-screen map centered on the user's current location
- Add a marker/pin for the user's own position
- This is the main screen of the app

---

### Step 4: Fetch Nearby Stalls

- Once you have the user's coordinates, call the API:
  ```
  GET http://<server>/stalls/nearby?lat={userLat}&lng={userLng}&radius=5
  ```
- Parse the JSON response into a list of stall objects
- Handle loading states and errors (no internet, server down)

---

### Step 5: Show Stalls on the Map

- For each stall returned by the API, place a marker/pin on the map at its coordinates
- Use a custom marker icon (e.g., a fruit/juice icon) to distinguish stalls from the user's location
- Cluster markers if there are many stalls close together

---

### Step 6: Stall Detail View

- When the user taps a stall marker, show a bottom sheet or detail card with:
  - Stall name
  - Description
  - Image (loaded from `image_url`)
  - Distance from user (returned by the API as `distance` in km)
- Optionally add a "Navigate" button that opens the device's maps app with directions to the stall

---

### Step 7: List View (Optional)

- Add a secondary view that shows stalls as a scrollable list instead of on the map
- Sort by distance (closest first - the API already returns them sorted)
- Each list item shows: name, description snippet, distance
- Tapping an item opens the detail view or centers the map on that stall

---

### Step 8: Admin Screen (For Stall Owners / You)

- A screen to add new stalls:
  - Text fields: name, description
  - Image picker: select a photo and upload it (you'll need to host images somewhere or add an upload endpoint later)
  - Location picker: tap on the map to set coordinates, or use current location
  - Submit button → calls `POST /stalls`
- A screen to edit/delete existing stalls:
  - Load stall data → pre-fill form
  - Save → calls `PUT /stalls/:id`
  - Delete → calls `DELETE /stalls/:id`

---

### Step 9: Search & Filter (Future)

- Search bar to find stalls by name
- Filter by distance radius (let user adjust the radius slider)
- Filter by type of juice/fruit (requires adding categories to the backend later)

---

### Step 10: Polish

- Add a splash screen with the app logo
- Loading skeletons while fetching data
- Empty state when no stalls are nearby
- Pull-to-refresh to reload stalls
- Offline caching of previously loaded stalls

---

## API Reference

See `API.md` in this same folder for full endpoint details, request/response formats, and examples.

---

## Architecture Suggestion

```
App
├── Screens
│   ├── MapScreen          (map + stall markers)
│   ├── StallDetailScreen  (info about a single stall)
│   ├── StallListScreen    (list view of nearby stalls)
│   └── AdminScreen        (add/edit/delete stalls)
├── Services
│   └── ApiService         (all HTTP calls to the backend)
├── Models
│   └── Stall              (data class matching the API response)
└── Utils
    └── LocationService    (GPS/location handling)
```

---

## Key Decisions to Make

| Decision | Options |
|----------|---------|
| Cross-platform or native? | Kotlin + Swift (native), or Kotlin Multiplatform, or React Native/Flutter |
| Image hosting | Cloud storage (S3, Cloudinary) or add an upload endpoint to the backend |
| Map provider | Google Maps (both platforms), Apple MapKit (iOS only) |
| State management | ViewModel (Android), ObservableObject (iOS), or equivalent |
