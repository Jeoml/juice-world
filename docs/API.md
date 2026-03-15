# Juice API Documentation

Base URL: `http://localhost:3000`

---

## Health Check

### `GET /`

Returns API status.

**Response:**
```json
{
  "name": "Juice API",
  "version": "1.0.0"
}
```

---

## Stalls

### Data Model

| Field        | Type    | Required | Description                        |
|------------- |---------|----------|------------------------------------|
| id           | integer | auto     | Unique identifier                  |
| name         | string  | yes      | Name of the fruit stall            |
| description  | string  | no       | Details about the stall            |
| image_url    | string  | no       | URL to the stall's image           |
| latitude     | number  | yes      | Latitude (-90 to 90)              |
| longitude    | number  | yes      | Longitude (-180 to 180)           |
| created_at   | string  | auto     | ISO datetime of creation           |
| updated_at   | string  | auto     | ISO datetime of last update        |

---

### `GET /stalls`

Returns all stalls, newest first.

**Response:** Array of stall objects.

```json
[
  {
    "id": 1,
    "name": "Fresh Mango Paradise",
    "description": "100% real mango pulp juice",
    "image_url": null,
    "latitude": 12.9716,
    "longitude": 77.5946,
    "created_at": "2026-03-14T21:02:07",
    "updated_at": "2026-03-14T21:02:07"
  }
]
```

---

### `GET /stalls/:id`

Returns a single stall by ID.

**URL Params:** `id` (integer)

**Success Response (200):**
```json
{
  "id": 1,
  "name": "Fresh Mango Paradise",
  "description": "100% real mango pulp juice",
  "image_url": null,
  "latitude": 12.9716,
  "longitude": 77.5946,
  "created_at": "2026-03-14T21:02:07",
  "updated_at": "2026-03-14T21:02:07"
}
```

**Error Response (404):**
```json
{ "error": "Stall not found" }
```

---

### `GET /stalls/nearby`

Returns stalls within a radius of given coordinates, sorted by distance (closest first).

**Query Parameters:**

| Param  | Type   | Required | Default | Description                  |
|--------|--------|----------|---------|------------------------------|
| lat    | number | yes      | -       | User's current latitude      |
| lng    | number | yes      | -       | User's current longitude     |
| radius | number | no       | 5       | Search radius in kilometers  |

**Example:** `GET /stalls/nearby?lat=12.97&lng=77.59&radius=10`

**Response:** Array of stall objects with an added `distance` field (in km).

```json
[
  {
    "id": 1,
    "name": "Fresh Mango Paradise",
    "description": "100% real mango pulp juice",
    "image_url": null,
    "latitude": 12.9716,
    "longitude": 77.5946,
    "created_at": "2026-03-14T21:02:07",
    "updated_at": "2026-03-14T21:02:07",
    "distance": 0.42
  }
]
```

**Error Response (400):**
```json
{ "error": "lat and lng are required query parameters" }
```

---

### `POST /stalls`

Creates a new stall.

**Headers:** `Content-Type: application/json`

**Request Body:**
```json
{
  "name": "Fresh Mango Paradise",
  "description": "100% real mango pulp juice",
  "image_url": "https://example.com/mango.jpg",
  "latitude": 12.9716,
  "longitude": 77.5946
}
```

**Response (200):** The created stall object.

---

### `PUT /stalls/:id`

Updates an existing stall. All fields in the body are required (full replacement).

**URL Params:** `id` (integer)

**Headers:** `Content-Type: application/json`

**Request Body:** Same as POST.

**Success Response (200):** The updated stall object.

**Error Response (404):**
```json
{ "error": "Stall not found" }
```

---

### `DELETE /stalls/:id`

Deletes a stall.

**URL Params:** `id` (integer)

**Success Response (200):**
```json
{ "message": "Stall deleted" }
```

**Error Response (404):**
```json
{ "error": "Stall not found" }
```

---

## Notes

- CORS is enabled, so frontend apps on any origin can call this API.
- All coordinates use decimal degrees (e.g., `12.9716`, not DMS format).
- The `nearby` endpoint uses the Haversine formula for distance calculation.
- The database is SQLite, stored as `juice.db` in the project root.
