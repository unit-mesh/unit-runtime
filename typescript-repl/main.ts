import { serve } from "@std/http/server.ts";
import { Hono, Context } from "hono";

import flowReplHandler from "./ws/flowReplHander.ts";

const app = new Hono();

app.get("/", (c) => c.text("Hello! Hono!"));
app.get("/repl", (ctx: Context) => flowReplHandler.hookOn(ctx));

serve(app.fetch);