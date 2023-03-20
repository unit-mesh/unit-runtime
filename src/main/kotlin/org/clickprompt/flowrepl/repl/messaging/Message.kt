package org.clickprompt.flowrepl.repl.messaging

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    var id: Int = -1,
    var resultValue: String,
    var className: String = "",
    var msgType: MessageType = MessageType.NONE,
    var content: MessageContent? = null,
)
