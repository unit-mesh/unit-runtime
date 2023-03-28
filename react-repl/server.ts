import { createServer } from "http";
import express from "express";
import next from "next";
import WebSocket from "ws";
import { InterpreterRequest, Message } from "./src/common/unit.types";
import { BUNDLE_SCRIPTS, compile } from "./src/common/compile";

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
  const wss = new WebSocket.Server({ server, path: "/repl" });

  wss.on("connection", (ws) => {
    ws.on("message", (message) => {
      const req: InterpreterRequest = JSON.parse(message.toString());
      const compiledCode = compile(`${req.id}.tsx`, req.code);

      const output: Message = {
        id: req.id,
        resultValue: compiledCode ?? "",
        className: "",
        msgType: "frontend",
        content: {
          scripts: BUNDLE_SCRIPTS,
        },
      };

      ws.send(JSON.stringify(output));
    });

    // Handle WebSocket errors
    ws.on("error", (error) => {
      console.error(`WebSocket error: ${error}`);
    });

    ws.on("close", () => {
      console.log("WebSocket disconnected");
    });
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
