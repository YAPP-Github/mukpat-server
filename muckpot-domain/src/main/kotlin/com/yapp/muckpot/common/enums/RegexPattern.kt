package com.yapp.muckpot.common.enums

enum class RegexPattern(
    private val pattern: String
) {
    EMAIL("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");

    fun matches(input: String): Boolean {
        return this.pattern.toRegex().matches(input)
    }
}
