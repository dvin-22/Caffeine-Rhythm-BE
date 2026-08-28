package com.caffeinerhythm.global.error

class BusinessException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.message)
