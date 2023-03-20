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

@RestController
class HelloController {
    @GetMapping("/hello")
    fun helloKotlin(): String {
        return "hello world"
    }
}

@SpringBootApplication
class KotlinDemoApplication

fun main(args: Array<String>) {
    SpringApplication.run(KotlinDemoApplication::class.java, *args)
}
        """.trimIndent())
    }
}