package com.caffeinerhythm.global.error

import org.springframework.http.HttpStatus

/**
 * API 명세서의 Error code 와 Message 를 그대로 옮긴다.
 * 사용자에게 그대로 노출되므로 문구를 임의로 바꾸지 않는다.
 */
enum class ErrorCode(
    val status: HttpStatus,
    val message: String,
) {
    OAUTH_FAILED(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "로그인이 만료되었어요. 다시 로그인해 주세요."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 인증 정보예요."),

    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "닉네임은 공백·특수문자 없이 10자 이내로 입력해 주세요."),
    INVALID_BIOMETRIC(HttpStatus.BAD_REQUEST, "올바른 신체 치수를 입력해 주세요."),
    SCHEDULE_OVERLAP(HttpStatus.BAD_REQUEST, "집중 시간과 취침 시간은 겹칠 수 없습니다."),
    BEVERAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "선택할 수 없는 음료가 포함되어 있어요."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "요청 형식이 올바르지 않아요."),

    RHYTHM_NOT_FOUND(HttpStatus.NOT_FOUND, "오늘의 리듬이 아직 없어요."),

    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "일시적인 오류가 발생했어요. 잠시 후 다시 시도해 주세요."),
    ;

    companion object {
        fun findByName(name: String?): ErrorCode? = entries.firstOrNull { it.name == name }
    }
}
