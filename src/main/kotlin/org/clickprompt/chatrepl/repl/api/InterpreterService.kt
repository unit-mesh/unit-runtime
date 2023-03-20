package org.clickprompt.chatrepl.repl.api

import org.clickprompt.chatrepl.repl.messaging.Message

interface InterpreterService {
    fun eval(interpreterRequest: InterpreterRequest): Message
}
