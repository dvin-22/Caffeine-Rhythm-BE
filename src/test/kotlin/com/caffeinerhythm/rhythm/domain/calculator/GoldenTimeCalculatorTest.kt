package com.caffeinerhythm.rhythm.domain.calculator

import com.caffeinerhythm.rhythm.domain.model.BeverageOption
import com.caffeinerhythm.rhythm.domain.model.BiometricProfile
import com.caffeinerhythm.rhythm.domain.model.GoldenTime
import com.caffeinerhythm.rhythm.domain.model.RhythmTimeline
import com.caffeinerhythm.rhythm.domain.model.Schedule
import com.caffeinerhythm.rhythm.domain.model.TimedDose
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GoldenTimeCalculatorTest {

    private val date = LocalDate.of(2026, 8, 28)
    private val americano = BeverageOption(1, "아메리카노", 125, energyDrink = false)
    private val coldBrew = BeverageOption(3, "콜드브루", 150, energyDrink = false)
    private val decaf = BeverageOption(5, "디카페인 아메리카노", 10, energyDrink = false)
    private val energy = BeverageOption(8, "에너지음료", 60, energyDrink = true)

    private fun timeline(
        focusStart: String = "10:00",
        focusEnd: String = "17:00",
        sleep: String = "23:00",
    ) = RhythmTimeline.of(
        date,
        Schedule(LocalTime.parse(focusStart), LocalTime.parse(focusEnd), LocalTime.parse(sleep), listOf(1)),
    )

    private fun calculate(
        profile: BiometricProfile = BiometricProfile(27, 70.0, 3),
        candidates: List<BeverageOption> = listOf(americano),
        timeline: RhythmTimeline = timeline(),
    ): GoldenTime = GoldenTimeCalculator.calculate(
        timeline = timeline,
        profile = profile,
        thresholds = ThresholdCalculator.calculate(profile),
        halfLifeHours = HalfLifeCalculator.calculate(profile),
        dailyLimitMg = DailyLimitCalculator.calculate(profile),
        candidates = candidates,
    )

    @Test
    fun `표준 사용자에게 최소 한 건을 추천한다`() {
        val result = calculate()

        assertTrue(result.recommendations.isNotEmpty())
        assertEquals(result.recommendations.sumOf { it.caffeineMg }, result.totalCaffeineMg)
    }

    @Test
    fun `첫 섭취는 집중 시작 한 시간 전쯤이다`() {
        val first = calculate().recommendations.first()

        assertEquals(timeline().focusStart.minusHours(1), first.startTime)
    }

    @Test
    fun `취침 시각 농도가 취침 상한을 넘지 않는다`() {
        val profile = BiometricProfile(27, 70.0, 3)
        val result = calculate(profile)
        val thresholds = ThresholdCalculator.calculate(profile)

        val doses = result.recommendations.map { TimedDose(it.endTime, it.caffeineMg.toDouble()) }
        val atSleep = ConcentrationCalculator.concentrationAt(
            doses,
            timeline().sleep,
            HalfLifeCalculator.calculate(profile),
            profile.effectiveWeightKg,
        )

        assertTrue(
            atSleep <= thresholds.sleepUpperMgPerL + 0.001,
            "취침 농도 $atSleep 가 상한 ${thresholds.sleepUpperMgPerL} 를 넘었다",
        )
    }

    @Test
    fun `하루 총량 상한을 넘지 않는다`() {
        val profile = BiometricProfile(16, 60.0, 3)
        val result = calculate(profile, listOf(americano))

        assertTrue(result.totalCaffeineMg <= DailyLimitCalculator.calculate(profile))
    }

    @Test
    fun `후보가 없으면 추천도 없다`() {
        val result = calculate(candidates = emptyList())

        assertTrue(result.empty)
        assertEquals(0, result.totalCaffeineMg)
    }

    @Test
    fun `디카페인만 골라도 하한 미달을 이유로 비우지 않는다`() {
        val result = calculate(candidates = listOf(decaf))

        assertTrue(result.recommendations.isNotEmpty(), "하한 도달은 목표이지 필수 조건이 아니다")
        assertTrue(result.recommendations.all { it.beverageId == decaf.id })
    }

    @Test
    fun `청소년이 에너지음료를 고르면 배수를 0_5로 고정한다`() {
        val result = calculate(BiometricProfile(16, 60.0, 3), listOf(energy))

        assertTrue(result.recommendations.isNotEmpty())
        assertTrue(result.recommendations.all { it.multiplier == 0.5 })
        assertTrue(result.recommendations.all { it.caffeineMg == 30 })
    }

    @Test
    fun `성인은 에너지음료를 한 캔으로 추천한다`() {
        val result = calculate(BiometricProfile(27, 70.0, 3), listOf(energy))

        assertTrue(result.recommendations.first().multiplier == 1.0)
    }

    @Test
    fun `취침 직전 집중이라 어떤 잔도 상한을 지킬 수 없으면 비워서 돌려준다`() {
        val result = calculate(
            profile = BiometricProfile(27, 70.0, 5),
            candidates = listOf(coldBrew),
            timeline = timeline("22:00", "22:30", "23:00"),
        )

        assertTrue(result.empty)
    }

    @Test
    fun `추천 구간은 시작이 끝보다 뒤일 수 없다`() {
        val result = calculate(candidates = listOf(americano, decaf, energy))

        assertTrue(result.recommendations.all { !it.startTime.isAfter(it.endTime) })
    }
}
