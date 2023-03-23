package org.clickprompt.unitserver.warpper.lang

/**
 *
 * ```kotlin
 * fun main() {
 *     val port = 8080
 *     val classToStart = Application::class.java.name
 *
 *     val ktClass = ::main::class.java.classLoader.loadClass(classToStart).kotlin
 *     val instance = (ktClass.primaryConstructor?.call() ?: ktClass.objectInstance) as? Kotless
 *
 *     val kotless = instance ?: error("The entry point ${"$"}classToStart does not inherit from "${'$'}{Kotless::class.qualifiedName}!")
 *
 *     val app = SpringApplication(kotless.bootKlass.java)
 *     app.setDefaultProperties(mapOf("server.port" to port.toString()))
 *     app.run()
 * }
 *
 * main()
 * ```
 */
class SpringLangBuilder: LangBuilder {
    override fun build(port: Int): String {
        return """

@SpringBootApplication
open class Application : Kotless() {
    override val bootKlass: KClass<*> = this::class
}

fun main() {
    val port = $port
    val classToStart = Application::class.java.name

    val ktClass = ::main::class.java.classLoader.loadClass(classToStart).kotlin
    val instance = (ktClass.primaryConstructor?.call() ?: ktClass.objectInstance) as? Kotless

    val kotless = instance ?: error("The entry point ${"$"}classToStart does not inherit from "${'$'}{Kotless::class.qualifiedName}!")

    val app = SpringApplication(kotless.bootKlass.java)
    app.setDefaultProperties(mapOf("server.port" to ${port}.toString()))
    app.run()
}

main()
 """
    }
}