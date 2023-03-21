package org.clickprompt.flowrepl.repl.compiler

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.clickprompt.chatrepl.compiler.toLibraries
import org.jetbrains.kotlinx.jupyter.EvalRequestData
import org.jetbrains.kotlinx.jupyter.ReplForJupyter
import org.jetbrains.kotlinx.jupyter.api.AfterCellExecutionCallback
import org.jetbrains.kotlinx.jupyter.api.ClassAnnotationHandler
import org.jetbrains.kotlinx.jupyter.api.Code
import org.jetbrains.kotlinx.jupyter.api.CodePreprocessor
import org.jetbrains.kotlinx.jupyter.api.ExecutionCallback
import org.jetbrains.kotlinx.jupyter.api.FieldHandler
import org.jetbrains.kotlinx.jupyter.api.FileAnnotationHandler
import org.jetbrains.kotlinx.jupyter.api.InternalVariablesMarker
import org.jetbrains.kotlinx.jupyter.api.InterruptionCallback
import org.jetbrains.kotlinx.jupyter.api.KotlinKernelVersion
import org.jetbrains.kotlinx.jupyter.api.RendererHandler
import org.jetbrains.kotlinx.jupyter.api.TextRendererWithPriority
import org.jetbrains.kotlinx.jupyter.api.ThrowableRenderer
import org.jetbrains.kotlinx.jupyter.api.libraries.ColorSchemeChangedCallback
import org.jetbrains.kotlinx.jupyter.api.libraries.KernelRepository
import org.jetbrains.kotlinx.jupyter.api.libraries.LibraryDefinition
import org.jetbrains.kotlinx.jupyter.api.libraries.LibraryResource
import org.jetbrains.kotlinx.jupyter.libraries.EmptyResolutionInfoProvider
import org.jetbrains.kotlinx.jupyter.libraries.LibraryResolver
import org.jetbrains.kotlinx.jupyter.messaging.NoOpDisplayHandler
import org.jetbrains.kotlinx.jupyter.repl.creating.createRepl
import org.jetbrains.kotlinx.jupyter.util.AcceptanceRule
import org.slf4j.LoggerFactory
import java.io.File

class KotlinReplWrapper {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val repl: ReplForJupyter

    init {
        this.repl = this.makeEmbeddedRepl()
    }

    private fun makeEmbeddedRepl(): ReplForJupyter {
        val property = System.getProperty("java.class.path")
        var embeddedClasspath: MutableList<File> = property.split(File.pathSeparator).map(::File).toMutableList()

// TODO: check for remote deploy of follow codes:
//
//        val isInRuntime = embeddedClasspath.size == 1
//        if (isInRuntime) {
//            System.setProperty("kotlin.script.classpath", property)
//
//            val compiler = KotlinJars.compilerClasspath
//            if (compiler.isNotEmpty()) {
//                val tempdir = compiler[0].parent
//                embeddedClasspath =
//                    File(tempdir).walk(FileWalkDirection.BOTTOM_UP).sortedBy { it.isDirectory }.toMutableList()
//            }
//        }

        embeddedClasspath = embeddedClasspath.distinctBy { it.name }
            .filter {
                // remove `logback-classic-1.2.11.jar` from classpath
                // because it conflicts with `logback-classic-1.2.3.jar` from `kotlinx-jupyter-core`
                !(it.name.startsWith("logback-classic-") && it.name.endsWith(".jar"))
            }
                as MutableList<File>

        logger.info("classpath: $embeddedClasspath")

        return createRepl(
            EmptyResolutionInfoProvider,
            embeddedClasspath,
            libraryResolver = resolveArchGuardDsl(),
            displayHandler = NoOpDisplayHandler,
            isEmbedded = true
        )
    }

    fun eval(code: Code, jupyterId: Int = -1, storeHistory: Boolean = true) =
        repl.evalEx(EvalRequestData(code, jupyterId, storeHistory))

    /**
     * TODO: add some thing like this.
     *
     * ```kotlin
     * repl.eval {
     *     addLibrary(testLibraryDefinition1)
     * }
     * val result1 = eval("org.jetbrains.kotlinx.jupyter.test.TestSum(5, 8)")
     * assertEquals(13, result1.renderedValue)
     * val result2 = eval(
     *     """
     *     import org.jetbrains.kotlinx.jupyter.test.TestFunList
     *     TestFunList(12, TestFunList(13, TestFunList(14, null)))
     *     """.trimIndent(),
     * )
     * assertEquals("[12, 13, 14]", result2.renderedValue)
     *```
     */
    fun evalWithPre(code: Code, jupyterId: Int = -1, storeHistory: Boolean = true) =
        repl.eval {
            // todo
        }

    companion object {
        fun resolveArchGuardDsl(): LibraryResolver {
            val springLibs = Json.encodeToString(
                SimpleLibraryDefinition(
                    imports = listOf(
                        "org.springframework.boot.*",
                        "org.springframework.boot.autoconfigure.*",
                        "org.springframework.web.bind.annotation.*",
                        "org.springframework.context.annotation.ComponentScan",
                        "org.springframework.context.annotation.Configuration"
                    ),
                    dependencies = listOf(
                        "org.springframework.boot:spring-boot-starter-web:2.7.9"
                    )
                )
            )
            val spring = "spring" to springLibs

            val mysql = "mysql" to Json.encodeToString(
                SimpleLibraryDefinition(dependencies = listOf("mysql:mysql-connector-java:8.0.32"))
            )

            return listOf(spring, mysql).toLibraries()
        }
    }
}

@Serializable
class SimpleLibraryDefinition(
    override var imports: List<String> = emptyList(),
    override var dependencies: List<String> = emptyList()
) : LibraryDefinition {
    @Transient
    override var options: Map<String, String> = emptyMap()

    @Transient
    override var repositories: List<KernelRepository> = emptyList()

    @Transient
    override var init: List<ExecutionCallback<*>> = emptyList()

    @Transient
    override var initCell: List<ExecutionCallback<*>> = emptyList()

    @Transient
    override var afterCellExecution: List<AfterCellExecutionCallback> = emptyList()

    @Transient
    override var shutdown: List<ExecutionCallback<*>> = emptyList()

    @Transient
    override var renderers: List<RendererHandler> = emptyList()

    @Transient
    override var textRenderers: List<TextRendererWithPriority> = emptyList()

    @Transient
    override var throwableRenderers: List<ThrowableRenderer> = emptyList()

    @Transient
    override var converters: List<FieldHandler> = emptyList()

    @Transient
    override var classAnnotations: List<ClassAnnotationHandler> = emptyList()

    @Transient
    override var fileAnnotations: List<FileAnnotationHandler> = emptyList()

    @Transient
    override var resources: List<LibraryResource> = emptyList()

    @Transient
    override var codePreprocessors: List<CodePreprocessor> = emptyList()

    @Transient
    override var internalVariablesMarkers: List<InternalVariablesMarker> = emptyList()

    @Transient
    override var minKernelVersion: KotlinKernelVersion? = null

    @Transient
    override var originalDescriptorText: String? = null

    @Transient
    override var integrationTypeNameRules: List<AcceptanceRule<String>> = emptyList()

    @Transient
    override var interruptionCallbacks: List<InterruptionCallback> = emptyList()

    @Transient
    override var colorSchemeChangedCallbacks: List<ColorSchemeChangedCallback> = emptyList()

}