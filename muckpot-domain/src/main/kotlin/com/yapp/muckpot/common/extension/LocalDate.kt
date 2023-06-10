package com.yapp.muckpot.common.extension

import java.time.LocalDate

fun LocalDate.isToday(): Boolean {
    return this == LocalDate.now()
}

fun LocalDate.isTomorrow(): Boolean {
    return this == LocalDate.now().plusDays(1)
}
