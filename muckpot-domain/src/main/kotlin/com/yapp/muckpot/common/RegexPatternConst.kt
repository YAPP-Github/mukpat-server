package com.yapp.muckpot.common

const val EMAIL = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"
const val MEETING_TIME = "^(오전|오후) (0[1-9]|1[0-2]):([0-5][0-9])$"
const val PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}\$"

const val ONLY_NAVER = "^[A-Za-z0-9._%+-]+@naver\\.com\$"
const val YYYYMMDD = "yyyy-MM-dd"
