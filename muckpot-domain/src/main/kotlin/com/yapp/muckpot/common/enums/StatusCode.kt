package com.yapp.muckpot.common.enums

enum class StatusCode(
    val code: Int
) {
    OK(200),
    CREATED(201),
    NO_CONTENT(204),

    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    INVALID_TOKEN(498),

    INTERNAL_SERVER(500);
}
