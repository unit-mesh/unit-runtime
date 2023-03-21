@file:Repository("https://packages.jetbrains.team/maven/p/ktls/maven")
@file:Repository("https://repo.maven.apache.org/maven2/")

@file:DependsOn("io.kotless:kotless-lang:0.2.0")
@file:DependsOn("io.kotless:kotless-lang-local:0.2.0")
@file:DependsOn("io.kotless:ktor-lang:0.2.0")
@file:DependsOn("io.kotless:ktor-lang-local:0.2.0")

@file:DependsOn("com.h2database:h2:2.1.212")

@file:DependsOn("org.jetbrains.exposed:exposed-core:0.40.1")
@file:DependsOn("org.jetbrains.exposed:exposed-dao:0.40.1")
@file:DependsOn("org.jetbrains.exposed:exposed-jdbc:0.40.1")

import io.kotless.dsl.ktor.KotlessAWS
import kotlin.reflect.full.primaryConstructor

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

data class User(val id: Int, val username: String)

class Server : KotlessAWS() {
    override fun prepare(app: Application) {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            SchemaUtils.create(Users)
        }

        app.routing {
            post("/register") {
                val user = call.receive<User>()
                val id = transaction {
                    // Insert the new user into the database
                    Users.insert {
                        it[username] = user.username
                    } get Users.id
                }

                val newUser = User(id, user.username)
                call.respond(newUser)
            }
        }
    }
}

object Users : org.jetbrains.exposed.sql.Table("users") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50).uniqueIndex()

    override val primaryKey = PrimaryKey(id, name = "PK_User_ID")
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

