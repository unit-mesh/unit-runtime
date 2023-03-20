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
package org.clickprompt.springbootkotlin      
            
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import java.util.*

@RestController
class HelloController {
    @GetMapping("/hello")
    fun helloKotlin(): String {
        return "hello world"
    }
}

@SpringBootApplication
open class ReplApplication

fun main(args: Array<String>) {
    val app = SpringApplication(ReplApplication::class.java)
    app.setDefaultProperties(
        Collections
            .singletonMap<String, Any>("server.port", "8083")
    )
    app.run()
}

main(arrayOf())
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