package com.caffeinerhythm.rhythm.domain.calculator

import com.caffeinerhythm.rhythm.domain.model.BiometricProfile
import kotlin.test.Test
import kotlin.test.assertEquals

class ThresholdCalculatorTest {

    private val tolerance = 0.001

    private fun thresholdsOf(level: Int?) =
        ThresholdCalculator.calculate(BiometricProfile(sensitivityLevel = level))

    @Test
    fun `민감도 보통이면 기준값 그대로`() {
        val thresholds = thresholdsOf(3)

        assertEquals(1.4, thresholds.focusLowerMgPerL, tolerance)
        assertEquals(4.7, thresholds.focusUpperMgPerL, tolerance)
        assertEquals(1.4, thresholds.sleepUpperMgPerL, tolerance)
    }

    @Test
    fun `민감도가 매우 높으면 취침 상한이 0_98 mg per L`() {
        // Baur 2024 의 EEG 영향 농도 약 5 µmol/L(≈0.97 mg/L)와 맞는지 확인한다.
        assertEquals(0.98, thresholdsOf(5).sleepUpperMgPerL, tolerance)
    }

    @Test
    fun `민감도가 낮을수록 기준선이 올라간다`() {
        assertEquals(1.82, thresholdsOf(1).focusLowerMgPerL, tolerance)
        assertEquals(1.61, thresholdsOf(2).focusLowerMgPerL, tolerance)
        assertEquals(1.19, thresholdsOf(4).focusLowerMgPerL, tolerance)
    }

    @Test
    fun `민감도 미입력이면 보통과 같다`() {
        assertEquals(thresholdsOf(3), thresholdsOf(null))
    }
}
