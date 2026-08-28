package com.caffeinerhythm.rhythm.domain.calculator

import com.caffeinerhythm.rhythm.domain.model.BiometricProfile
import kotlin.test.Test
import kotlin.test.assertEquals

class HalfLifeCalculatorTest {

    private fun halfLifeOf(age: Int?) =
        HalfLifeCalculator.calculate(BiometricProfile(ageYears = age))

    @Test
    fun `나이 구간별 반감기`() {
        assertEquals(4.5, halfLifeOf(14))
        assertEquals(4.5, halfLifeOf(19))
        assertEquals(5.0, halfLifeOf(20))
        assertEquals(5.0, halfLifeOf(39))
        assertEquals(5.5, halfLifeOf(40))
        assertEquals(5.5, halfLifeOf(59))
        assertEquals(6.5, halfLifeOf(60))
        assertEquals(6.5, halfLifeOf(100))
    }

    @Test
    fun `나이 미입력이면 성인 표준 5시간`() {
        assertEquals(5.0, halfLifeOf(null))
    }
}
