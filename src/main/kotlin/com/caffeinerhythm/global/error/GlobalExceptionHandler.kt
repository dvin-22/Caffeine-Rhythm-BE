package com.caffeinerhythm.global.error

import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

/**
 * 컨트롤러에 try-catch 를 두지 않는다. 모든 에러 응답은 여기서 ProblemDetail + code 로 나간다.
 *
 * ResponseEntityExceptionHandler 를 상속해 404·405·415 같은 Spring MVC 자체 예외는
 * 프레임워크 처리를 그대로 두고, 우리 예외만 덧붙인다.
 */
@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(BusinessException::class)
    fun handleBusiness(e: BusinessException): ProblemDetail = problemDetail(e.errorCode)

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(e: Exception): ProblemDetail {
        log.error("처리하지 못한 예외", e)
        return problemDetail(ErrorCode.INTERNAL_ERROR)
    }

    /**
     * DTO 검증 실패. 어떤 코드로 내려갈지는 제약 애노테이션의 message 에 ErrorCode 이름을 적어 정한다.
     * 예: @field:Pattern(regexp = ..., message = "INVALID_NICKNAME")
     */
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val errorCode = ex.bindingResult.fieldErrors
            .firstNotNullOfOrNull { ErrorCode.findByName(it.defaultMessage) }
            ?: ErrorCode.INVALID_REQUEST
        return ResponseEntity.status(errorCode.status).body(problemDetail(errorCode))
    }

    private fun problemDetail(errorCode: ErrorCode): ProblemDetail =
        ProblemDetail.forStatusAndDetail(errorCode.status, errorCode.message).apply {
            setProperty("code", errorCode.name)
        }
}
