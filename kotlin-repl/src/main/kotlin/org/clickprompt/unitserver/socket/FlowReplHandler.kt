package org.clickprompt.unitserver.socket

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.clickprompt.unitserver.repl.KotlinInterpreter
import org.clickprompt.unitserver.repl.api.InterpreterRequest
import org.slf4j.LoggerFactory
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession

class FlowReplHandler : WebSocketHandler {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    private lateinit var replServer: KotlinInterpreter

    override fun afterConnectionEstablished(session: WebSocketSession) {
        // Todo: find a way to inject the replServer, when connect
        replServer = KotlinInterpreter()
        logger.info("onOpen WebSocket")
    }

    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
        val request: InterpreterRequest = Json.decodeFromString(message.payload.toString())
        val result = replServer.eval(request)
        emit(session, Json.encodeToString(result))
    }

    fun emit(session: WebSocketSession, msg: String) = session.sendMessage(TextMessage(msg))
    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
    }

    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        logger.info("onClose WebSocket")
    }

    override fun supportsPartialMessages(): Boolean {
        return false
    }

}
