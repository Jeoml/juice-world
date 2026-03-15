import { Database } from "bun:sqlite";

const db = new Database("juice.db");

db.run("PRAGMA journal_mode = WAL");
db.run("PRAGMA foreign_keys = ON");

db.run(`
  CREATE TABLE IF NOT EXISTS stalls (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    image_url TEXT,
    latitude REAL NOT NULL,
    longitude REAL NOT NULL,
    created_at TEXT DEFAULT (datetime('now')),
    updated_at TEXT DEFAULT (datetime('now'))
  )
`);

export default db;
