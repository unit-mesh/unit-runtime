package org.clickprompt.unitserver.magic

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class LangCodeWrapperTest {
    private val INSERTED_CODE = """
@SpringBootApplication
open class Application : Kotless() {
    override val bootKlass: KClass<*> = this::class
}

fun main() {
    val port = 8080
    val classToStart = Application::class.java.name

    val ktClass = ::main::class.java.classLoader.loadClass(classToStart).kotlin
    val instance = (ktClass.primaryConstructor?.call() ?: ktClass.objectInstance) as? Kotless

    val kotless = instance ?: error("The entry point ${"$"}classToStart does not inherit from "${"$"}{Kotless::class.qualifiedName}!")

    val app = SpringApplication(kotless.bootKlass.java)
    app.setDefaultProperties(mapOf("server.port" to 8080.toString()))
    app.run()
}

main()"""

    @Test
    fun langBuilder() {
        val sourceCode = """%use spring            

@RestController
object Pages {
    @GetMapping("/")
    fun main() = "Hello World!"
}

@ComponentScan(basePackageClasses = [Pages::class])
@SpringBootApplication
open class Application : Kotless() {
    override val bootKlass: KClass<*> = this::class
}

""".trimIndent()
        val port = 8080
        val output = LangCodeWrapper.wrapper(sourceCode, port)
        assertNotNull(output)

        val expected = """%use spring            

@RestController
object Pages {
    @GetMapping("/")
    fun main() = "Hello World!"
}

@ComponentScan(basePackageClasses = [Pages::class])
@SpringBootApplication
open class Application : Kotless() {
    override val bootKlass: KClass<*> = this::class
}


@SpringBootApplication
open class Application : Kotless() {
    override val bootKlass: KClass<*> = this::class
}

fun main() {
    val classToStart = Application::class.java.name

    val ktClass = ::main::class.java.classLoader.loadClass(classToStart).kotlin
    val instance = (ktClass.primaryConstructor?.call() ?: ktClass.objectInstance) as? Kotless

    val kotless = instance ?: error("instance inherit from Kotless!")

    val app = SpringApplication(kotless.bootKlass.java)
    app.setDefaultProperties(mapOf("server.port" to 8080.toString()))
    app.run()
}

main()
"""
        assertEquals(expected, output)
    }

    @Test
    fun return_origin_function() {
        val sourceCode = """val a = 1 """
        val port = 8080
        val output = LangCodeWrapper.wrapper(sourceCode, port)
        assertNotNull(output)
    }

    @Test
    fun test_has_magic() {
        val sourceCode = """%use spring"""
        val output = LangCodeWrapper.hasLang(sourceCode)
        assertTrue(output)
    }

    @Test
    fun test_with_ktor() {
        val sourceCode = """%use ktor
            
class Server : KotlessAWS() {
    override fun prepare(app: Application) {
        app.routing {
            get("/") {
                call.respondText { "Hello World!" }
            }
        }
    }
}
"""
        val port = 8080
        val output = LangCodeWrapper.wrapper(sourceCode, port)
        assertNotNull(output)
        val expected = """%use ktor
            
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
    val classToStart = Server::class.java.name

    val ktClass = ::main::class.java.classLoader.loadClass(classToStart).kotlin
    val instance = (ktClass.primaryConstructor?.call() ?: ktClass.objectInstance) as? KotlessAWS

    val kotless = instance ?: error("instance inherit from Kotless!")

    embeddedServer(Netty, 8080) {
        kotless.prepare(this)
    }.start(wait = true)
}

main()

""".trimIndent()
        assertEquals(output, expected)
    }
}
