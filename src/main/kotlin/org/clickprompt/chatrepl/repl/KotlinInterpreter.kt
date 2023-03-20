package org.clickprompt.chatrepl.repl

import org.clickprompt.chatrepl.repl.api.InterpreterRequest
import org.clickprompt.chatrepl.repl.messaging.ErrorContent
import org.clickprompt.chatrepl.repl.messaging.Message
import org.clickprompt.chatrepl.repl.messaging.MessageType
import org.clickprompt.chatrepl.repl.compiler.KotlinReplWrapper
import org.jetbrains.kotlinx.jupyter.repl.EvalResultEx
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class KotlinInterpreter  {
    private var compiler: KotlinReplWrapper = KotlinReplWrapper()
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun eval(interpreterRequest: InterpreterRequest): Message {
        try {
            val result = compiler.eval(interpreterRequest.code, interpreterRequest.id)
            return convertResult(result, interpreterRequest.id)
        } catch (e: Exception) {
            logger.error(e.toString())
            val content = ErrorContent(e.javaClass.name, e.toString())
            return Message(interpreterRequest.id, "", "", MessageType.ERROR, content = content)
        }
    }

    private fun convertResult(result: EvalResultEx, id: Int): Message {
        val resultValue = result.rawValue
        val className: String = resultValue?.javaClass?.name.orEmpty()

        val message = Message(
            id,
            resultValue.toString(),
            className
        )

        return message
    }
}