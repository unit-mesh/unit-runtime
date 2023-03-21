import { InterpreterRequest } from "./InterpreterRequest.ts";
import { Message } from "../messaging/Message.ts";

interface InterpreterService {
  eval(interpreterRequest: InterpreterRequest): Message;
}
