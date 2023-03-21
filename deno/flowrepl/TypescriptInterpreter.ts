import { logger } from "../logger.ts";
import { InterpreterRequest } from "./api/InterpreterRequest.ts";
import { Message } from "./messaging/Message.ts";


export class TypescriptInterpreter {
    eval(request: InterpreterRequest): Message {
        logger.info("evaluating request: " + request.code);
        return eval(request.code)
    }
}