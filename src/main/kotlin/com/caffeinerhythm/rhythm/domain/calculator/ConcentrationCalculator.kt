package com.caffeinerhythm.rhythm.domain.calculator

import com.caffeinerhythm.rhythm.domain.model.ConcentrationPoint
import com.caffeinerhythm.rhythm.domain.model.TimedDose
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.exp
import kotlin.math.ln

/* 1구획 1차 흡수 모델.

    C(t) = (F · D / Vd) × ka/(ka − ke) × ( e^(−ke·t) − e^(−ka·t) )

기준은 체내 잔여량(mg)이 아니라 혈장 농도(mg/L)다. 그래야 체중이 계산에 반영된다.
출처: Alsabri et al. (2018) */
object ConcentrationCalculator {

    fun singleDose(
        doseMg: Double,
        elapsedHours: Double,
        halfLifeHours: Double,
        weightKg: Double,
    ): Double {
        if (elapsedHours <= 0.0) return 0.0

        val volumeOfDistribution = CaffeineConstants.VOLUME_OF_DISTRIBUTION_PER_KG * weightKg
        val eliminationRate = ln(2.0) / halfLifeHours
        val absorptionRate = CaffeineConstants.ABSORPTION_RATE_PER_HOUR

        return (CaffeineConstants.BIOAVAILABILITY * doseMg / volumeOfDistribution) *
            (absorptionRate / (absorptionRate - eliminationRate)) *
            (exp(-eliminationRate * elapsedHours) - exp(-absorptionRate * elapsedHours))
    }

    fun concentrationAt(
        intakes: List<TimedDose>,
        at: LocalDateTime,
        halfLifeHours: Double,
        weightKg: Double,
    ): Double = intakes.sumOf { intake ->
        singleDose(intake.doseMg, hoursBetween(intake.at, at), halfLifeHours, weightKg)
    }

    /* 한 건을 마신 뒤 농도가 [targetMgPerL] 아래로 내려오기까지 걸리는 시간.
    최고 농도 이후 구간은 단조 감소하므로 이분 탐색으로 찾는다. */
    fun hoursUntilBelow(
        doseMg: Double,
        targetMgPerL: Double,
        halfLifeHours: Double,
        weightKg: Double,
    ): Double {
        var low = PEAK_HOURS
        var high = SEARCH_UPPER_BOUND_HOURS

        if (singleDose(doseMg, low, halfLifeHours, weightKg) <= targetMgPerL) return 0.0

        repeat(BISECTION_STEPS) {
            val mid = (low + high) / 2
            if (singleDose(doseMg, mid, halfLifeHours, weightKg) > targetMgPerL) low = mid else high = mid
        }
        return (low + high) / 2
    }

    fun curve(
        intakes: List<TimedDose>,
        from: LocalDateTime,
        to: LocalDateTime,
        halfLifeHours: Double,
        weightKg: Double,
        intervalMinutes: Long = CaffeineConstants.CURVE_INTERVAL_MINUTES,
    ): List<ConcentrationPoint> {
        val points = mutableListOf<ConcentrationPoint>()
        var at = from
        while (!at.isAfter(to)) {
            points += ConcentrationPoint(at, concentrationAt(intakes, at, halfLifeHours, weightKg))
            at = at.plusMinutes(intervalMinutes)
        }
        return points
    }

    private fun hoursBetween(from: LocalDateTime, to: LocalDateTime): Double =
        Duration.between(from, to).toMillis() / MILLIS_PER_HOUR

    private const val MILLIS_PER_HOUR = 3_600_000.0
    private const val PEAK_HOURS = 0.75
    private const val SEARCH_UPPER_BOUND_HOURS = 48.0
    private const val BISECTION_STEPS = 60
}
