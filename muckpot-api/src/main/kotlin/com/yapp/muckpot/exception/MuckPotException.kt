package com.yapp.muckpot.exception

import com.yapp.muckpot.common.BaseErrorCode
import java.lang.RuntimeException

class MuckPotException(
    val errorCode: BaseErrorCode
) : RuntimeException()
