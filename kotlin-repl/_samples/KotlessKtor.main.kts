@file:Repository("https://packages.jetbrains.team/maven/p/ktls/maven")
@file:Repository("https://repo.maven.apache.org/maven2/")

@file:DependsOn("io.kotless:kotless-lang:0.2.0")
@file:DependsOn("io.kotless:kotless-lang-local:0.2.0")
@file:DependsOn("io.kotless:ktor-lang:0.2.0")
@file:DependsOn("io.kotless:ktor-lang-local:0.2.0")

import io.kotless.dsl.ktor.KotlessAWS
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlin.reflect.full.primaryConstructor

class Server : KotlessAWS() {
    override fun prepare(app: Application) {
        app.routing {
            get("/") {
                call.respondText { "Hello World!" }
            }
        }
    }
}

fun main() {
    val port = 8080
    val classToStart = Server::class.java.name

    val ktClass = ::main::class.java.classLoader.loadClass(classToStart).kotlin
    val instance = (ktClass.primaryConstructor?.call() ?: ktClass.objectInstance) as? KotlessAWS

    val kotless = instance ?: error("The entry point $classToStart does not inherit from ${KotlessAWS::class.qualifiedName}!")

    embeddedServer(Netty, port) {
        kotless.prepare(this)
    }.start(wait = true)
}

main()
