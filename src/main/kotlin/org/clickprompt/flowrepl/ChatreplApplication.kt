package org.clickprompt.flowrepl

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.util.*


@SpringBootApplication
class ChatreplApplication

fun main(args: Array<String>) {
    val app = SpringApplication(ChatreplApplication::class.java)
    app.setDefaultProperties(
        Collections
            .singletonMap<String, Any>("server.port", "8083")
    )
    app.run()
}

