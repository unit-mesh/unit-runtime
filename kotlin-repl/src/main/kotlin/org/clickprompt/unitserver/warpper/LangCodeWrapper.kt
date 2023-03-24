package org.clickprompt.unitserver.warpper

import org.clickprompt.unitserver.warpper.lang.KtorLangBuilder
import org.clickprompt.unitserver.warpper.lang.SpringLangBuilder

object LangCodeWrapper {
    val supportedLangs: List<String> = listOf("spring", "ktor", "kotless")

    fun hasLang(code: String): Boolean {
        val langs = SimpleMagicMatcher().parseLang(code)
        return langs.any {
            supportedLangs.contains(it)
        }
    }

    fun wrapper(code: String, port: Int): String{
        val langs = SimpleMagicMatcher().parseLang(code)
        if (langs.isEmpty()) {
            return code
        }

        val usedLang = langs.firstOrNull {
            supportedLangs.contains(it)
        }

        val lang = when (usedLang) {
            "spring" -> SpringLangBuilder(code, port).build()
            "kotless", "ktor" -> KtorLangBuilder(code, port).build()
            else -> null
        }

        return lang?: code
    }
}
