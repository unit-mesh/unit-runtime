package org.clickprompt.flowrepl.repl.api

import kotlinx.serialization.Serializable

@Serializable
data class InterpreterRequest(
    var id: Int = -1,
    val code: String,
    val history: Boolean = false
)
