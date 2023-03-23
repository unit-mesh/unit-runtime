package org.clickprompt.unitserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class UnitServerApplication

fun main(args: Array<String>) {
    runApplication<UnitServerApplication>(*args)
}

