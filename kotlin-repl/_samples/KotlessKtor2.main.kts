@file:Repository("https://packages.jetbrains.team/maven/p/ktls/maven")
@file:Repository("https://repo.maven.apache.org/maven2/")

@file:DependsOn("io.kotless:kotless-lang:0.2.0")
@file:DependsOn("io.kotless:kotless-lang-local:0.2.0")
@file:DependsOn("io.kotless:ktor-lang:0.2.0")
@file:DependsOn("io.kotless:ktor-lang-local:0.2.0")

import io.kotless.dsl.ktor.KotlessAWS
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.sql.*
import kotlin.reflect.full.primaryConstructor

/**
 * features: User Registration
 * POST /register
 * input: { "username": "user", "password": "password" }
 * output: { "id": 1, "username": "user" }
 */
class Server : KotlessAWS() {
    override fun prepare(app: Application) {
        app.routing {
            post("/register") {
                val input = call.receive<RegisterInput>()
                val userId = insertUser(input.username, input.password)
                call.respond(RegisterOutput(userId, input.username))
            }
        }
    }

    data class RegisterInput(val username: String, val password: String)
    data class RegisterOutput(val id: Int, val username: String)

    companion object {
        fun insertUser(username: String, password: String): Int {
            val connectionUrl = System.getenv("DATABASE_URL")
            DriverManager.getConnection(connectionUrl).use { connection ->
                val statement = connection.prepareStatement(
                    "INSERT INTO users (username, password) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
                )
                statement.setString(1, username)
                statement.setString(2, password)
                statement.executeUpdate()
                val generatedKeys = statement.generatedKeys
                generatedKeys.next()
                return generatedKeys.getInt(1)
            }
        }
    }
}

fun main(port: Int) {
    val classToStart = Server::class.java.name

    val ktClass = ::main::class.java.classLoader.loadClass(classToStart).kotlin
    val instance = (ktClass.primaryConstructor?.call() ?: ktClass.objectInstance) as? KotlessAWS

    val kotless =
        instance ?: error("The entry point $classToStart does not inherit from ${KotlessAWS::class.qualifiedName}!")

    embeddedServer(Netty, port) {
        kotless.prepare(this)
    }.start(wait = true)
}

main(8848)

