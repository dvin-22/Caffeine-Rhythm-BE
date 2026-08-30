package com.caffeinerhythm.rhythm.domain.calculator

import com.caffeinerhythm.rhythm.domain.model.BiometricProfile

/* 반감기는 나이만 반영한다. 체중은 Vd(농도)에만, 민감도는 임계값에만 쓴다.
건강한 성인 반감기 3~7시간, 중앙값 약 5시간. */
object HalfLifeCalculator {

    fun calculate(profile: BiometricProfile): Double = when (profile.effectiveAgeYears) {
        in 14..19 -> 4.5
        in 20..39 -> 5.0
        in 40..59 -> 5.5
        else -> 6.5
    }
}
