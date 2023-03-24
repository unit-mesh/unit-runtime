package org.clickprompt.unitserver.warpper

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SimpleMagicMatcherTest {
    private val matcher = SimpleMagicMatcher()

    @Test
    fun parseMagic() {
        val code = """%use mysql
%use spring
"""
        val langs = matcher.parseLang(code)
        assertEquals(2, langs.size)
        assertEquals("mysql", langs[0])
        assertEquals("spring", langs[1])
    }

    @Test
    fun parseMagicWithCellMarker() {
        val code = """%use mysql, spring
"""
        val magics = matcher.parseLang(code)
        assertEquals(2, magics.size)
        assertEquals("mysql", magics[0])
        assertEquals("spring", magics[1])
    }
}