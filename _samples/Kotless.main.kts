@file:Repository("https://packages.jetbrains.team/maven/p/ktls/maven")
@file:DependsOn("io.kotless:kotless-lang:0.2.0")
@file:DependsOn("io.kotless:spring-boot-lang:0.2.0")
@file:DependsOn("io.kotless:spring-boot-lang-local:0.2.0")
@file:DependsOn("io.kotless:spring-lang-parser:0.2.0")

import io.kotless.dsl.spring.Kotless
import org.springframework.boot.autoconfigure.SpringBootApplication
import kotlin.reflect.KClass
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
object Pages {
    @GetMapping("/")
    fun main() = "Hello World!"
}

@SpringBootApplication
open class Application : Kotless() {
    override val bootKlass: KClass<*> = this::class
}
