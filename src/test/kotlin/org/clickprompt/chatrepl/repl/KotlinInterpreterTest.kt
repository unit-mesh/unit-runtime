package org.clickprompt.chatrepl.repl


import org.clickprompt.chatrepl.repl.compiler.KotlinReplWrapper
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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {
 
    @GetMapping("/hello")
    fun helloKotlin(): String {
        return "hello world"
    }
}
        """.trimIndent())
    }
}