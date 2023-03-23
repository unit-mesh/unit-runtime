package org.clickprompt.unitserver.repl.api

import org.clickprompt.unitserver.repl.messaging.Message

interface InterpreterService {
    fun eval(interpreterRequest: InterpreterRequest): Message
}
