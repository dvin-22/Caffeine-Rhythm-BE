package com.caffeinerhythm.rhythm.domain.calculator

import com.caffeinerhythm.rhythm.domain.model.BiometricProfile
import com.caffeinerhythm.rhythm.domain.model.Thresholds

/* 성인 표준 기준값에 민감도 계수 k를 곱한다.
집중 하한과 취침 상한이 같은 값인 것은 "각성이 시작되는 농도"가
곧 "잠을 방해하기 시작하는 농도"이기 때문이다. */
object ThresholdCalculator {

    private val SENSITIVITY_FACTORS = mapOf(
        1 to 1.3,
        2 to 1.15,
        3 to 1.0,
        4 to 0.85,
        5 to 0.7,
    )

    fun calculate(profile: BiometricProfile): Thresholds {
        val k = SENSITIVITY_FACTORS.getValue(profile.effectiveSensitivityLevel)
        return Thresholds(
            focusLowerMgPerL = CaffeineConstants.FOCUS_LOWER_BASE_MG_PER_L * k,
            focusUpperMgPerL = CaffeineConstants.FOCUS_UPPER_BASE_MG_PER_L * k,
            sleepUpperMgPerL = CaffeineConstants.SLEEP_UPPER_BASE_MG_PER_L * k,
        )
    }
}
