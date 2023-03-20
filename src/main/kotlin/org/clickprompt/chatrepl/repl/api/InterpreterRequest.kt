package org.clickprompt.chatrepl.repl.api

import kotlinx.serialization.Serializable

@Serializable
data class InterpreterRequest(
    var id: Int = -1,
    val code: String
)
