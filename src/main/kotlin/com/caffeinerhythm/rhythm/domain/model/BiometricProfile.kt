package com.caffeinerhythm.rhythm.domain.model

import com.caffeinerhythm.global.error.BusinessException
import com.caffeinerhythm.global.error.ErrorCode
import com.caffeinerhythm.rhythm.domain.calculator.CaffeineConstants

/* 나이·체중·민감도. 전부 선택 입력, null 은 미입력을 뜻한다.
표준값은 저장하지 않고 계산 시점에만 effective* 로 적용한다. */
data class BiometricProfile(
    val ageYears: Int? = null,
    val weightKg: Double? = null,
    val sensitivityLevel: Int? = null,
) {
    init {
        if (ageYears != null && ageYears !in AGE_RANGE) throw BusinessException(ErrorCode.INVALID_BIOMETRIC)
        if (weightKg != null && weightKg !in WEIGHT_RANGE) throw BusinessException(ErrorCode.INVALID_BIOMETRIC)
        if (sensitivityLevel != null && sensitivityLevel !in SENSITIVITY_RANGE) {
            throw BusinessException(ErrorCode.INVALID_BIOMETRIC)
        }
    }

    val effectiveAgeYears: Int get() = ageYears ?: CaffeineConstants.DEFAULT_AGE_YEARS
    val effectiveWeightKg: Double get() = weightKg ?: CaffeineConstants.DEFAULT_WEIGHT_KG
    val effectiveSensitivityLevel: Int get() = sensitivityLevel ?: CaffeineConstants.DEFAULT_SENSITIVITY_LEVEL

    /* 나이 미입력은 성인 표준으로 처리한다. */
    val youth: Boolean get() = ageYears != null && ageYears in CaffeineConstants.YOUTH_AGES

    companion object {
        val AGE_RANGE = 14..100
        val WEIGHT_RANGE = 1.0..200.0
        val SENSITIVITY_RANGE = 1..5
    }
}
