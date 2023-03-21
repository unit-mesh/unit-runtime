package org.clickprompt.flowrepl.repl.api

import org.clickprompt.flowrepl.repl.messaging.Message

interface InterpreterService {
    fun eval(interpreterRequest: InterpreterRequest): Message
}
