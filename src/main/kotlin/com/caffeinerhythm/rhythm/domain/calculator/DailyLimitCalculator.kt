package com.caffeinerhythm.rhythm.domain.calculator

import com.caffeinerhythm.rhythm.domain.model.BiometricProfile
import kotlin.math.floor

object DailyLimitCalculator {

    /* 청소년의 상한은 체중에 비례한다.
    체중 미입력 청소년은 Vd 계산의 표준 70kg 대신 55kg를 써서 보수적으로 잡는다. */
    fun calculate(profile: BiometricProfile): Int {
        if (!profile.youth) return CaffeineConstants.ADULT_DAILY_LIMIT_MG

        val weightKg = profile.weightKg ?: CaffeineConstants.YOUTH_FALLBACK_WEIGHT_KG
        return floor(CaffeineConstants.YOUTH_DAILY_LIMIT_MG_PER_KG * weightKg).toInt()
    }
}
