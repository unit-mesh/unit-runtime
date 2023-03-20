package org.clickprompt.flowrepl.repl


import org.clickprompt.flowrepl.repl.compiler.KotlinReplWrapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import io.kotest.matchers.shouldBe

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
    internal fun spring_helloworld() {
        compiler.eval("""            
// SLf4j 1.7 can accidentially be pulled in using Spring Boot 3 leading to exceptions
// https://github.com/spring-projects/spring-boot/issues/33854
@file:DependsOn("org.springframework.boot:spring-boot-starter-web:2.7.9")
@file:DependsOn("mysql:mysql-connector-java:8.0.32")

package sample

import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.web.bind.annotation.*
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.*

@RestController
class SampleController {
    @GetMapping("/hello")
    fun helloKotlin(): String {
        return "hello world"
    }
}

// @Configuration
// @ComponentScan
@SpringBootApplication
open class ReplApplication

fun main(args: Array<String>) {
    SpringApplication(ReplApplication::class.java).run(*args)
}

main(arrayOf("--server.port=8083"))
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
}