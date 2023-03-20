package org.clickprompt.chatrepl

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ChatreplApplication

fun main(args: Array<String>) {
	runApplication<ChatreplApplication>(*args)
}
