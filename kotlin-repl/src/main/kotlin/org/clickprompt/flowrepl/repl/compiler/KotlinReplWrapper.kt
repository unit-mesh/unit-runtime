package org.clickprompt.flowrepl.repl.compiler

import org.clickprompt.chatrepl.compiler.toLibraries
import org.jetbrains.kotlinx.jupyter.EvalRequestData
import org.jetbrains.kotlinx.jupyter.ReplForJupyter
import org.jetbrains.kotlinx.jupyter.api.Code
import org.jetbrains.kotlinx.jupyter.libraries.EmptyResolutionInfoProvider
import org.jetbrains.kotlinx.jupyter.libraries.LibraryResolver
import org.jetbrains.kotlinx.jupyter.messaging.NoOpDisplayHandler
import org.jetbrains.kotlinx.jupyter.repl.creating.createRepl
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
            val lib = "spring" to """
            {
                "imports": [
                    "org.springframework.*"
                ],
                "init": []
            }
                """.trimIndent()

            return listOf(lib).toLibraries()
        }
    }
}

