import { createServer } from "http";
import express from "express";
import next from "next";
import WebSocket from "ws";

// refs:
// - https://github.com/vercel/next.js/blob/canary/examples/custom-server/server.ts
// - https://nextjs.org/docs/advanced-features/custom-server
// - https://github.com/websockets/ws

const port = parseInt(process.env.PORT || "8080", 10);
const dev = process.env.NODE_ENV !== "production";
const app = next({ dev });
const nextHandler = app.getRequestHandler();

app.prepare().then(() => {
  const expressApp = express();
  const server = createServer(expressApp);
  const wss = new WebSocket.Server({ server });

  expressApp.get("/repl", function (req, res) {
    wss.clients.forEach((client) => {
      if (client.readyState === WebSocket.OPEN) {
        // Note: we add a `time` attribute to help with the UI state management
        client.send(JSON.stringify({ type: "buttons:yes", time: new Date() }));
      }
    });

    res.send("Hello World");
  });

  expressApp.all("*", (req, res) => {
    return nextHandler(req, res);
  });

  server.listen(port);

  console.info(`> Ready on http://localhost:${port}`);
  server.on("error", (err: any) => {
    if (err.code === "EADDRINUSE") {
      console.log("Address in use, retrying...");
      setTimeout(() => {
        server.close();
        server.listen(port);
      }, 1000);
    }
  });
});
