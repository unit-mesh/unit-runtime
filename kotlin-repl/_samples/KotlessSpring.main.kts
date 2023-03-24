@file:Repository("https://packages.jetbrains.team/maven/p/ktls/maven")
@file:Repository("https://repo.maven.apache.org/maven2/")

@file:DependsOn("org.springframework.boot:spring-boot-starter-web:2.7.9")
@file:DependsOn("io.kotless:kotless-lang:0.2.0")
@file:DependsOn("io.kotless:spring-boot-lang:0.2.0")
@file:DependsOn("io.kotless:spring-boot-lang-local:0.2.0")
@file:DependsOn("io.kotless:spring-lang-parser:0.2.0")

import io.kotless.dsl.spring.Kotless
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.GetMapping
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor


@RestController
class SampleController {
    @GetMapping("/hello")
    fun helloKotlin(): String {
        return "hello world"
    }
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
