package org.clickprompt.flowrepl.repl.compiler

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.clickprompt.chatrepl.compiler.toLibraries
import org.jetbrains.kotlinx.jupyter.libraries.LibraryResolver


val mysqlLibDef = SimpleLibraryDefinition(
    imports = listOf("java.sql.*"),
    dependencies = listOf("mysql:mysql-connector-java:8.0.32")
)

val springLibDef = SimpleLibraryDefinition(
    imports = listOf(
        "org.springframework.boot.*",
        "org.springframework.boot.autoconfigure.*",
        "org.springframework.web.bind.annotation.*",
        "org.springframework.context.annotation.ComponentScan",
        "org.springframework.context.annotation.Configuration"
    ),
    dependencies = listOf(
        "org.springframework.boot:spring-boot-starter-web:2.7.9"
    )
)

val KotlessLibDef = SimpleLibraryDefinition(
    imports = listOf(
        "import io.kotless.dsl.ktor.*",
        "import io.ktor.application.*",
        "import io.ktor.request.*",
        "import io.ktor.response.*",
        "import io.ktor.routing.*",
        "import io.ktor.server.engine.*",
        "import io.ktor.server.netty.*",
        "import kotlin.reflect.KClass",
        "import kotlin.reflect.full.primaryConstructor"
    ),
    dependencies = listOf(
        "io.kotless:kotless-lang:0.2.0",
        "io.kotless:kotless-lang-local:0.2.0",
        "io.kotless:spring-boot-lang:0.2.0",
        "io.kotless:spring-boot-lang-local:0.2.0",
        "io.kotless:spring-lang-parser:0.2.0",
        "io.kotless:ktor-lang:0.2.0",
        "io.kotless:ktor-lang-local:0.2.0"
    ),
)

fun extendLibraries(): LibraryResolver {
    val spring = "spring" to Json.encodeToString<SimpleLibraryDefinition>(
        springLibDef
    )
    val mysqlLibs = "mysql" to Json.encodeToString(
        mysqlLibDef
    )

    val kotless = "kotless" to Json.encodeToString(
        KotlessLibDef
    )


    return listOf(spring, mysqlLibs, kotless).toLibraries()
}
