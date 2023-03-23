package org.clickprompt.unitserver.repl.api

import kotlinx.serialization.Serializable

@Serializable
data class InterpreterRequest(
    var id: Int = -1,
    val code: String,
    val language: String = "kotlin",
    val framework: String = SupportedFramewrok.SPRING.toString(),
    val history: Boolean = false
)

enum class SupportedFramewrok {
    SPRING, KTOR;

    companion object {
        fun fromString(value: String): SupportedFramewrok {
            return when (value.lowercase()) {
                "spring" -> SPRING
                "ktor" -> KTOR
                else -> throw IllegalArgumentException("Unknown framework: $value")
            }
        }
    }
}
