package com.yapp.muckpot.common

import java.util.*

object RandomCodeUtil {

    fun generateRandomCode(): String {
        val random = Random()
        val codeLength = 6
        val digits = "0123456789"
        val sb = StringBuilder(codeLength)

        for (i in 0 until codeLength) {
            val randomIndex = random.nextInt(digits.length)
            val randomDigit = digits[randomIndex]
            sb.append(randomDigit)
        }

        return sb.toString()
    }
}
