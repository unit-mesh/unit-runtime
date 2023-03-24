package org.clickprompt.unitserver.warpper

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class LangWrapperTest {
    @Test
    fun langBuilder() {
        val lang = "spring"
        val port = 8080
        val langBuilder = LangWrapper.wrapper(lang, port)
        assertNotNull(langBuilder)
    }
}