package com.caffeinerhythm.rhythm.domain.calculator

import com.caffeinerhythm.rhythm.domain.model.TimedDose
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/* docs/03-도메인분석및구현전략.md 3-5 의 검증 표를 그대로 옮긴다.
표준 성인 기준: 70kg, 반감기 5.0시간. */
class ConcentrationCalculatorTest {

    private val weightKg = 70.0
    private val halfLife = 5.0
    private val sleepUpper = 1.4

    @Test
    fun `100mg을 마시면 4시간 뒤 취침 상한 근처로 내려온다`() {
        val concentration = ConcentrationCalculator.singleDose(
            doseMg = 100.0,
            elapsedHours = 4.0,
            halfLifeHours = halfLife,
            weightKg = weightKg,
        )

        assertEquals(1.4, concentration, 0.05)
    }

    @Test
    fun `100mg은 취침 4_0시간 전이 마감이다`() {
        val hours = ConcentrationCalculator.hoursUntilBelow(
            doseMg = 100.0,
            targetMgPerL = sleepUpper,
            halfLifeHours = halfLife,
            weightKg = weightKg,
        )

        assertEquals(4.0, hours, 0.05)
    }

    @Test
    fun `아메리카노 한 잔 125mg은 취침 5_6시간 전이 마감이다`() {
        val hours = ConcentrationCalculator.hoursUntilBelow(
            doseMg = 125.0,
            targetMgPerL = sleepUpper,
            halfLifeHours = halfLife,
            weightKg = weightKg,
        )

        assertEquals(5.6, hours, 0.05)
    }

    @Test
    fun `최고 농도는 섭취 45분 뒤쯤에 나온다`() {
        val peak = (0..180 step 5).maxBy { minutes ->
            ConcentrationCalculator.singleDose(100.0, minutes / 60.0, halfLife, weightKg)
        }

        assertTrue(peak in 40..50, "최고 농도 도달 시각이 ${peak}분")
    }

    @Test
    fun `여러 건은 시점별로 더한다`() {
        val base = LocalDateTime.of(2026, 8, 28, 9, 0)
        val intakes = listOf(
            TimedDose(base, 100.0),
            TimedDose(base.plusHours(3), 100.0),
        )

        val at = base.plusHours(5)
        val total = ConcentrationCalculator.concentrationAt(intakes, at, halfLife, weightKg)
        val first = ConcentrationCalculator.singleDose(100.0, 5.0, halfLife, weightKg)
        val second = ConcentrationCalculator.singleDose(100.0, 2.0, halfLife, weightKg)

        assertEquals(first + second, total, 0.0001)
    }

    @Test
    fun `아직 마시지 않은 시각의 농도는 0이다`() {
        val base = LocalDateTime.of(2026, 8, 28, 9, 0)
        val intakes = listOf(TimedDose(base.plusHours(2), 100.0))

        assertEquals(0.0, ConcentrationCalculator.concentrationAt(intakes, base, halfLife, weightKg))
    }

    @Test
    fun `체중이 가벼우면 같은 양에도 농도가 높다`() {
        val light = ConcentrationCalculator.singleDose(100.0, 1.0, halfLife, 50.0)
        val heavy = ConcentrationCalculator.singleDose(100.0, 1.0, halfLife, 90.0)

        assertTrue(light > heavy)
    }

    @Test
    fun `곡선은 요청한 간격으로 끝점까지 만든다`() {
        val from = LocalDateTime.of(2026, 8, 28, 7, 0)
        val to = from.plusHours(2)

        val curve = ConcentrationCalculator.curve(
            intakes = listOf(TimedDose(from, 125.0)),
            from = from,
            to = to,
            halfLifeHours = halfLife,
            weightKg = weightKg,
            intervalMinutes = 10,
        )

        assertEquals(13, curve.size)
        assertEquals(from, curve.first().at)
        assertEquals(to, curve.last().at)
    }
}
