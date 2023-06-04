package com.yapp.muckpot.common

import com.yapp.muckpot.domains.user.controller.dto.UserResponse
import com.yapp.muckpot.domains.user.exception.UserErrorCode
import com.yapp.muckpot.exception.MuckPotException
import org.springframework.security.core.context.SecurityContextHolder

/**
 * SecurityContextHolder 에서 유저 정보를 가져온다.
 *
 * filter에서 authentication이 초기화가 수행된다.
 * @see com.yapp.muckpot.filter.JwtAuthorizationFilter
 *
 * 로그인 유저: 항상 nullable 하지 않아야 한다.
 * 비로그인 유저: 예외를 던진다.
 */
object SecurityContextHolderUtil {
    fun getCurrentUserId(): Long {
        return ((SecurityContextHolder.getContext().authentication?.credentials) as? UserResponse)?.userId
            ?: throw MuckPotException(UserErrorCode.USER_NOT_FOUND)
    }
}
