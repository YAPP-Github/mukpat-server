package com.yapp.muckpot.common.constants

const val ACCESS_TOKEN_KEY = "accessToken"
const val REFRESH_TOKEN_KEY = "refreshToken"
const val JWT_LOGOUT_VALUE = "logout"

const val USER_CLAIM = "user"
const val USER_EMAIL_CLAIM = "email"

const val ACCESS_TOKEN_SECONDS = 3600L
// TODO 테스트 후 원복
const val REFRESH_TOKEN_BASIC_SECONDS = 540L
const val REFRESH_TOKEN_KEEP_SECONDS = 540L
//const val REFRESH_TOKEN_BASIC_SECONDS = 3600 * 24 * 30L
//const val REFRESH_TOKEN_KEEP_SECONDS = 3600 * 24 * 180L
