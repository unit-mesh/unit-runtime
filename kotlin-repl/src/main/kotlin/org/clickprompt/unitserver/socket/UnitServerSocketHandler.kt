package org.clickprompt.unitserver.socket

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.clickprompt.unitserver.messaging.Message
import org.clickprompt.unitserver.messaging.MessageType
import org.clickprompt.unitserver.messaging.UnitServerContent
import org.clickprompt.unitserver.interpreter.KotlinInterpreter
import org.clickprompt.unitserver.interpreter.api.InterpreterRequest
import org.clickprompt.unitserver.magic.LangCodeWrapper
import org.slf4j.LoggerFactory
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession


object PortGenerator {
    fun generate(): Int {
        return (10000..20000).random()
    }
}

@OptIn(DelicateCoroutinesApi::class)
class UnitServerSocketHandler : WebSocketHandler {
    private var lastJob: Job? = null
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private lateinit var replServer: KotlinInterpreter


    override fun afterConnectionEstablished(session: WebSocketSession) {
        replServer = KotlinInterpreter()
        logger.info("onOpen WebSocket")
    }

    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {

        val request: InterpreterRequest = Json.decodeFromString(message.payload.toString())

        val isUnitServer = LangCodeWrapper.hasLang(request.code)
        if (isUnitServer) {
            val port = PortGenerator.generate()
            request.port = port
            request.code = LangCodeWrapper.wrapper(request.code, port)
        }

        if (lastJob != null) {
            lastJob?.cancel()
        }

        // todo: change to Thread for eval, and closed after new request ? with same id ?
        if (isUnitServer) {
            var result = Message(request.id, "", "", "", MessageType.RUNNING)
            GlobalScope.launch {
                lastJob = this.coroutineContext.job

                result = replServer.eval(request)
            }

            if (result.msgType == MessageType.RUNNING) {
                var url = "http://localhost:${request.port}"
                if (LangCodeWrapper.isKotless(request.code)) {
                    url = "http://0.0.0.0:${request.port}"
                }

                result.content = UnitServerContent(url)
                emit(session, Json.encodeToString(result))
            }

            return
        }

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
