package org.clickprompt.unitserver.warpper

import org.clickprompt.unitserver.warpper.lang.SpringLangBuilder

object LangWrapper {
    val supportedLangs: List<String> = listOf("spring")
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
            else -> null
        }

        return lang?: code
    }
}