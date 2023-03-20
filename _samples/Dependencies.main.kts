@file:DependsOn("org.springframework.boot:spring-boot-starter-web:2.7.9")

import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.*

@Controller
class LocalSampleController {
    @GetMapping("/hello")
    fun helloKotlin(): String {
        return "hello world"
    }
}

@Configuration
@ComponentScan(basePackageClasses = [LocalSampleController::class])
@SpringBootApplication
open class ReplApplication

fun main(args: Array<String>) {
    runApplication<ReplApplication>(*args)
}

main(arrayOf("--server.port=8083"))
