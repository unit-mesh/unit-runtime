package org.clickprompt.unitserver.warpper

import org.clickprompt.unitserver.warpper.lang.SpringLangBuilder

object LangDispatcher {
    fun dispatch(lang: String, port: Int): String? {
        val langBuilder = when (lang) {
            "spring" -> SpringLangBuilder()
            else -> null
        }

        return langBuilder?.build(port)
    }
}