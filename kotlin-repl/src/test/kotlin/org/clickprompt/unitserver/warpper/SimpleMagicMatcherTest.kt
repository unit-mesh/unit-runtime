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
        val magics = matcher.parseLang(code)
        assertEquals(2, magics.size)
        assertEquals("%use mysql", magics[0])
        assertEquals("%use spring", magics[1])
    }

    @Test
    fun parseMagicWithCellMarker() {
        val code = """%use mysql, spring
"""
        val magics = matcher.parseLang(code)
        assertEquals(1, magics.size)
        assertEquals("%use mysql, spring", magics[0])
    }
}