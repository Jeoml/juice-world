import { Elysia } from "elysia";
import { cors } from "@elysiajs/cors";
import { stallRoutes } from "./src/stalls";

const app = new Elysia()
  .use(cors())
  .use(stallRoutes)
  .get("/", () => ({ name: "Juice API", version: "1.0.0" }))
  .listen(3000);

console.log(`Juice API running at http://localhost:${app.server!.port}`);
