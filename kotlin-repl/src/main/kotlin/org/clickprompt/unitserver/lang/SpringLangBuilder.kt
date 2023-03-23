package org.clickprompt.unitserver.lang

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
class SpringLangBuilder {
    companion object {
        fun build(port: Int, appName: String): String {
            return """
fun main() {
    val port = $port
    val classToStart = $appName::class.java.name

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
}