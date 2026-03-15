import { Elysia, t } from "elysia";
import db from "./db";

const stallBody = {
  body: t.Object({
    name: t.String(),
    description: t.Optional(t.String()),
    image_url: t.Optional(t.String()),
    latitude: t.Number({ minimum: -90, maximum: 90 }),
    longitude: t.Number({ minimum: -180, maximum: 180 }),
  }),
};

const stallParams = {
  params: t.Object({
    id: t.Numeric(),
  }),
};

export const stallRoutes = new Elysia({ prefix: "/stalls" })

  // Get all stalls
  .get("/", () => {
    return db.query("SELECT * FROM stalls ORDER BY created_at DESC").all();
  })

  // Get a single stall
  .get("/:id", ({ params: { id }, set }) => {
    const stall = db.query("SELECT * FROM stalls WHERE id = ?").get(id);
    if (!stall) {
      set.status = 404;
      return { error: "Stall not found" };
    }
    return stall;
  }, stallParams)

  // Get nearby stalls (within radius in km)
  .get("/nearby", ({ query, set }) => {
    const lat = parseFloat(query.lat);
    const lng = parseFloat(query.lng);
    const radius = parseFloat(query.radius || "5"); // default 5km

    if (isNaN(lat) || isNaN(lng)) {
      set.status = 400;
      return { error: "lat and lng are required query parameters" };
    }

    // Haversine approximation using SQLite
    // 6371 = Earth's radius in km
    const stalls = db.query(`
      SELECT *,
        (6371 * acos(
          cos(radians(?1)) * cos(radians(latitude)) *
          cos(radians(longitude) - radians(?2)) +
          sin(radians(?1)) * sin(radians(latitude))
        )) AS distance
      FROM stalls
      HAVING distance <= ?3
      ORDER BY distance
    `).all(lat, lng, radius);

    return stalls;
  })

  // Create a stall
  .post("/", ({ body }) => {
    const result = db.query(`
      INSERT INTO stalls (name, description, image_url, latitude, longitude)
      VALUES (?1, ?2, ?3, ?4, ?5)
    `).run(body.name, body.description ?? null, body.image_url ?? null, body.latitude, body.longitude);

    return db.query("SELECT * FROM stalls WHERE id = ?").get(result.lastInsertRowid);
  }, stallBody)

  // Update a stall
  .put("/:id", ({ params: { id }, body, set }) => {
    const existing = db.query("SELECT * FROM stalls WHERE id = ?").get(id);
    if (!existing) {
      set.status = 404;
      return { error: "Stall not found" };
    }

    db.query(`
      UPDATE stalls
      SET name = ?1, description = ?2, image_url = ?3, latitude = ?4, longitude = ?5, updated_at = datetime('now')
      WHERE id = ?6
    `).run(body.name, body.description ?? null, body.image_url ?? null, body.latitude, body.longitude, id);

    return db.query("SELECT * FROM stalls WHERE id = ?").get(id);
  }, { ...stallBody, ...stallParams })

  // Delete a stall
  .delete("/:id", ({ params: { id }, set }) => {
    const existing = db.query("SELECT * FROM stalls WHERE id = ?").get(id);
    if (!existing) {
      set.status = 404;
      return { error: "Stall not found" };
    }

    db.query("DELETE FROM stalls WHERE id = ?").run(id);
    return { message: "Stall deleted" };
  }, stallParams);
