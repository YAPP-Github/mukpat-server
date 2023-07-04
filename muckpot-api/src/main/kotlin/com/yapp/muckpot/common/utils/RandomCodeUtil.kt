package com.yapp.muckpot.common.utils

import java.security.SecureRandom

object RandomCodeUtil {

    fun generateRandomCode(): String {
        val random = SecureRandom()
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
