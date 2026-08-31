package com.caffeinerhythm.rhythm.domain.calculator

import com.caffeinerhythm.rhythm.domain.model.BiometricProfile
import kotlin.test.Test
import kotlin.test.assertEquals

class DailyLimitCalculatorTest {

    private fun limitOf(age: Int?, weight: Double? = null) =
        DailyLimitCalculator.calculate(BiometricProfile(ageYears = age, weightKg = weight))

    @Test
    fun `만 19세 이상은 400mg`() {
        assertEquals(400, limitOf(19, 60.0))
        assertEquals(400, limitOf(45, 90.0))
    }

    @Test
    fun `만 14에서 18세는 체중 1kg당 2_5mg`() {
        assertEquals(150, limitOf(16, 60.0))
        assertEquals(112, limitOf(14, 45.0))
    }

    @Test
    fun `체중을 입력하지 않은 청소년은 55kg 기준 137mg`() {
        assertEquals(137, limitOf(16))
    }

    @Test
    fun `나이 미입력은 성인 표준과 같이 400mg`() {
        assertEquals(400, limitOf(null))
        assertEquals(400, limitOf(null, 50.0))
    }
}
