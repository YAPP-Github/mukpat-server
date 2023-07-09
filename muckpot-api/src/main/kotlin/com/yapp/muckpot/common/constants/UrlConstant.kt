package com.yapp.muckpot.common.constants

const val LOGIN_URL = "/api/v2/users/login"
const val SIGN_UP_URL = "/api/v2/users"

const val EMAIL_REQUEST_URL = "/api/v2/emails/request"
const val EMAIL_VERIFY_URL = "/api/v2/emails/verify"
const val USER_PROFILE_URL = "/api/v1/users/profile"
const val LOGOUT_URL = "/api/v1/users/logout"
const val REISSUE_JWT_URL = "/api/v1/users/refresh"

@Deprecated("V2 배포 후 제거")
const val SIGN_UP_URL_V1 = "/api/v1/users"

@Deprecated("V2 배포 후 제거")
const val LOGIN_URL_V1 = "/api/v1/users/login"

@Deprecated("V2 배포 후 제거")
const val EMAIL_REQUEST_URL_V1 = "/api/v1/emails/request"

@Deprecated("V2 배포 후 제거")
const val EMAIL_VERIFY_URL_V1 = "/api/v1/emails/verify"
