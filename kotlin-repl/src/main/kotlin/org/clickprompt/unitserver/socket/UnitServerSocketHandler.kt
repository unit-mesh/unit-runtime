package org.clickprompt.unitserver.socket

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.clickprompt.unitserver.messaging.UnitServerContent
import org.clickprompt.unitserver.repl.KotlinInterpreter
import org.clickprompt.unitserver.repl.api.InterpreterRequest
import org.clickprompt.unitserver.warpper.LangCodeWrapper
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
        // Todo: find a way to inject the replServer, when connect
        replServer = KotlinInterpreter()
        logger.info("onOpen WebSocket")
    }

    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {

        val request: InterpreterRequest = Json.decodeFromString(message.payload.toString())

        val isUnitServer = LangCodeWrapper.hasLang(request.code)
        if (isUnitServer) {
            request.code = LangCodeWrapper.wrapper(request.code, PortGenerator.generate())
        }

        // todo: change to Thread for eval, and closed after new request ? with same id ?
        if (isUnitServer) {
            if (lastJob != null) {
                lastJob?.cancel()
            }

            val job = GlobalScope.launch {
                val result = replServer.eval(request)
                emit(session, Json.encodeToString(result))
                result.content = UnitServerContent(url = """http://localhost:${request.port}/""")
            }

            this.lastJob = job
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
