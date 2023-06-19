package com.yapp.muckpot.common

const val ACCESS_TOKEN_KEY = "accessToken"
const val REFRESH_TOKEN_KEY = "refreshToken"
const val JWT_LOGOUT_VALUE = "logout"

const val USER_CLAIM = "user"
const val USER_EMAIL_CLAIM = "email"

const val ACCESS_TOKEN_BASIC_SECONDS = 3600L
const val ACCESS_TOKEN_KEEP_SECONDS = 3600 * 24L
const val REFRESH_TOKEN_BASIC_SECONDS = 3600 * 24 * 7L
const val REFRESH_TOKEN_KEEP_SECONDS = 3600 * 24 * 30L
