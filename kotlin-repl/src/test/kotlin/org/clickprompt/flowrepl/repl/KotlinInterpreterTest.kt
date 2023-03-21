package org.clickprompt.flowrepl.repl


import org.clickprompt.flowrepl.repl.compiler.KotlinReplWrapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import io.kotest.matchers.shouldBe
import io.kotless.dsl.spring.Kotless
import org.junit.jupiter.api.Disabled

class KotlinInterpreterTest {
    private lateinit var compiler: KotlinReplWrapper

    @BeforeEach
    internal fun setUp() {
        this.compiler = KotlinReplWrapper()
    }

    @Test
    internal fun simple_eval() {
        compiler.eval("val x = 3")
        val res = compiler.eval("x*2")
        res.rawValue shouldBe 6
    }

    @Test
    internal fun spring_hello_world() {
        compiler.eval("""            
package sample

%use mysql
%use spring

import java.nio.file.Paths
import java.util.*

@RestController
class SampleController {
    @GetMapping("/hello")
    fun helloKotlin(): String {
        return "hello world"
    }
}

@ComponentScan(basePackageClasses = [SampleController::class])
@SpringBootApplication
open class ReplApplication

fun main(args: Array<String>) {
    SpringApplication(ReplApplication::class.java).run(*args)
}

main(arrayOf("--server.port=8083"))

%trackClasspath
%trackExecution generated
""".trimIndent())
    }

    @Test
    internal fun mysql_connector_demo() {
        compiler.eval("""import java.sql.*

fun main() {
    val url = "jdbc:mysql://localhost:3306/test"
    val username = "root"
    val password = "prisma"

    try {
        // Load the JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver")

        // Establish the connection
        val conn = DriverManager.getConnection(url, username, password)

        // Print a message indicating that the connection was successful
        println("Connected to the database")

        // Close the connection
        conn.close()
    } catch (e: SQLException) {
        // Handle any SQL errors
        println("SQLException: ${"$"}{e.message}")
    } catch (e: ClassNotFoundException) {
        // Handle any errors in loading the JDBC driver
        println("ClassNotFoundException: ${"$"}{e.message}")
    }
}

main()
""")
    }

    @Test
    @Disabled
    fun kotless_helloworld() {
        compiler.eval("""@file:Repository("https://packages.jetbrains.team/maven/p/ktls/maven")
@file:Repository("https://repo.maven.apache.org/maven2/")

@file:DependsOn("org.springframework.boot:spring-boot-starter-web:2.7.9")
@file:DependsOn("io.kotless:kotless-lang:0.2.0")
@file:DependsOn("io.kotless:spring-boot-lang:0.2.0")
@file:DependsOn("io.kotless:spring-boot-lang-local:0.2.0")
@file:DependsOn("io.kotless:spring-lang-parser:0.2.0")

import io.kotless.Constants
import io.kotless.dsl.spring.Kotless
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import kotlin.reflect.KClass
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.reflect.full.primaryConstructor
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

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

fun main() {
    val port = 8080
    val classToStart = Application::class.java.name

    val ktClass = ::main::class.java.classLoader.loadClass(classToStart).kotlin
    val instance = (ktClass.primaryConstructor?.call() ?: ktClass.objectInstance) as? Kotless

    val kotless = instance ?: error("The entry point ${"$"}classToStart does not inherit from ${Kotless::class.qualifiedName}!")

    val app = SpringApplication(kotless.bootKlass.java)
    app.setDefaultProperties(mapOf("server.port" to port.toString()))
    app.run()
}

main()""")
    }

    @Test
    fun kotless_helloworld2() {
        compiler.eval(
            """@file:Repository("https://packages.jetbrains.team/maven/p/ktls/maven")
@file:Repository("https://repo.maven.apache.org/maven2/")

@file:DependsOn("io.kotless:kotless-lang:0.2.0")
@file:DependsOn("io.kotless:kotless-lang-local:0.2.0")

import io.kotless.dsl.lang.http.Get

@Get("/")
fun main() = "Hello world!"

main()
    """
        )
    }
}