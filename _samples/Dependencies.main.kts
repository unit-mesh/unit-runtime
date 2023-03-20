@file:DependsOn("org.springframework.boot:spring-boot-starter-webflux:3.0.4")
@file:DependsOn("org.springframework.boot:spring-boot-starter-web:3.0.4")
@file:DependsOn("ch.qos.logback:logback-classic:1.4.6")
@file:DependsOn("org.slf4j:slf4j-api:2.0.7")

import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.web.bind.annotation.*
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

fun main() {
    val app = SpringApplication(ReplApplication::class.java)
    app.setDefaultProperties(
        Collections
            .singletonMap<String, Any>("server.port", "8083")
    )
    app.run()
}

main()
