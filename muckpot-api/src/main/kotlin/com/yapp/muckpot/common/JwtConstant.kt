package com.yapp.muckpot.common

const val USER_CLAIM = "user"
const val USER_EMAIL_CLAIM = "email"

const val LOGIN_URL = "/api/v1/users/login"
const val SIGN_UP_URL = "/api/v1/users"
const val EMAIL_REQUEST = "/api/v1/emails/request"
const val EMAIL_VERIFY = "/api/v1/emails/verify"
const val USER_PROFILE_URL = "/v1/users/profile"

const val ACCESS_TOKEN_BASIC_SECONDS = 3600L
const val ACCESS_TOKEN_KEEP_SECONDS = 3600 * 24L
const val REFRESH_TOKEN_BASIC_SECONDS = 3600 * 24 * 7L
const val REFRESH_TOKEN_KEEP_SECONDS = 3600 * 24 * 30L
