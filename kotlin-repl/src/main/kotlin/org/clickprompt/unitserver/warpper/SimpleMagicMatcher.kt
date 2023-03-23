package org.clickprompt.unitserver.warpper

class SimpleMagicMatcher {
    private val MAGICS_SIGN = '%'
    private val MAGICS_REGEX = Regex("^$MAGICS_SIGN.*$", RegexOption.MULTILINE)
    private val CELL_MARKER_REGEX = Regex("""\A\s*#%%.*$""", RegexOption.MULTILINE)

    fun parseMagic(code: String): List<String> {
        val maybeFirstMatch = CELL_MARKER_REGEX.find(code, 0)
        val seed = maybeFirstMatch ?: MAGICS_REGEX.find(code, 0)
        return generateSequence(seed) {
            MAGICS_REGEX.find(code, it.range.last + 1)
        }.map {
            it.value
        }.toList()
    }
}
