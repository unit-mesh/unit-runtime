package org.clickprompt.flowrepl.repl

import org.clickprompt.flowrepl.repl.api.InterpreterRequest
import org.clickprompt.flowrepl.repl.messaging.ErrorContent
import org.clickprompt.flowrepl.repl.messaging.Message
import org.clickprompt.flowrepl.repl.messaging.MessageType
import org.clickprompt.flowrepl.repl.compiler.KotlinReplWrapper
import org.jetbrains.kotlinx.jupyter.api.toJson
import org.jetbrains.kotlinx.jupyter.repl.EvalResultEx
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class KotlinInterpreter {
    private var compiler: KotlinReplWrapper = KotlinReplWrapper()
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun eval(request: InterpreterRequest): Message {
        return try {
            val result = compiler.eval(request.code, request.id, request.history)
            convertResult(result, request.id)
        } catch (e: Exception) {
            logger.error(e.toString())
            val content = ErrorContent(e.javaClass.name, e.toString())
            Message(request.id, "", "", "", MessageType.ERROR, content = content)
        }
    }

    private fun convertResult(result: EvalResultEx, id: Int): Message {
        val resultValue = result.rawValue
        val className: String = resultValue?.javaClass?.name.orEmpty()

        return Message(
            id,
            resultValue.toString(),
            className,
            result.displayValue.toJson().toString()
        )
    }
}