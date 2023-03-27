package org.clickprompt.unitserver.magic

import org.clickprompt.unitserver.magic.lang.KtorLangBuilder
import org.clickprompt.unitserver.magic.lang.SpringLangBuilder

object LangCodeWrapper {
    val supportedLangs: List<String> = listOf("spring", "ktor")

    fun hasLang(code: String): Boolean {
        val langs = SimpleMagicMatcher().parseLang(code)
        return langs.any {
            supportedLangs.contains(it)
        }
    }

    fun isKotless(code: String): Boolean {
        val langs = SimpleMagicMatcher().parseLang(code)
        return langs.any {
            it == "ktor" || it == "kotless"
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
            "ktor" -> KtorLangBuilder(code, port).build()
            else -> null
        }

        return lang?: code
    }
}
