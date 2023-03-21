import { Context} from "hono"

import { TypescriptInterpreter } from "../flowrepl/TypescriptInterpreter.ts";

import {logger} from "../logger.ts";


class FlowReplHandler {
    private ctx: Context | null = null;
    private interpreter = new TypescriptInterpreter();

    constructor() {
        this.hookOn = this.hookOn.bind(this);
        this.emit = this.emit.bind(this);
     }

    hookOn(ctx: Context) {
        this.ctx = ctx;
        const {response, socket} = Deno.upgradeWebSocket(ctx.req.raw);

        socket.addEventListener("open", () => logger.info("WebSocket opened"));
        socket.addEventListener("close", () => logger.info("WebSocket closed"));

        socket.addEventListener("message", (e) => {
            const result = this.interpreter.eval(JSON.parse(e.data))
            this.emit(socket, result);
        });
        return response;
    }

    emit(socket: WebSocket, data: any) {
        if (socket.readyState === WebSocket.OPEN) {
            socket.send(JSON.stringify(data));
        }
    }
}

export default new FlowReplHandler();