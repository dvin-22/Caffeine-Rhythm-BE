package com.caffeinerhythm.rhythm.domain.calculator

import com.caffeinerhythm.rhythm.domain.model.BeverageOption
import com.caffeinerhythm.rhythm.domain.model.BiometricProfile
import com.caffeinerhythm.rhythm.domain.model.GoldenTime
import com.caffeinerhythm.rhythm.domain.model.RecommendedIntake
import com.caffeinerhythm.rhythm.domain.model.RhythmTimeline
import com.caffeinerhythm.rhythm.domain.model.Thresholds
import com.caffeinerhythm.rhythm.domain.model.TimedDose
import java.time.LocalDateTime
import kotlin.math.roundToInt

/* 골든타임 역산.
집중 시작 1시간 전을 첫 섭취로 잡고, 집중 구간에서 농도가 하한 아래로 떨어질 무렵마다 후보 음료를 한 잔 또는 반 잔 얹는다. 
취침 상한·집중 상한·하루 총량을 모두 지킨다.
하한 도달은 목표이지 필수 조건이 아니다. 디카페인만 골라 하한에 못 미치더라도 가능한 만큼은 제안한다. */
object GoldenTimeCalculator {

    private data class Option(
        val beverage: BeverageOption,
        val multiplier: Double,
        val caffeineMg: Int,
    )

    fun calculate(
        timeline: RhythmTimeline,
        profile: BiometricProfile,
        thresholds: Thresholds,
        halfLifeHours: Double,
        dailyLimitMg: Int,
        candidates: List<BeverageOption>,
    ): GoldenTime {
        val options = buildOptions(candidates, profile)
        if (options.isEmpty()) return GoldenTime.NONE

        val weightKg = profile.effectiveWeightKg
        val recommendations = mutableListOf<RecommendedIntake>()

        // 사용자가 구간의 시작에 마신다고 보고 그린 곡선
        val nominalDoses = mutableListOf<TimedDose>()

        // 구간의 끝에 마셔도 취침 상한을 넘지 않는지 보기 위한 최악의 경우
        val latestDoses = mutableListOf<TimedDose>()

        var totalMg = 0

        var at = timeline.focusStart.minusMinutes(CaffeineConstants.FIRST_INTAKE_LEAD_MINUTES)
        while (!at.isAfter(timeline.focusEnd) && recommendations.size < CaffeineConstants.MAX_RECOMMENDATIONS) {
            // 지금 마시면 최고 농도에 닿을 시각이 집중 구간을 벗어나면 의미가 없다
            val peakAt = at.plusMinutes(CaffeineConstants.PEAK_DELAY_MINUTES)
            if (peakAt.isAfter(timeline.focusEnd)) break

            val projected = ConcentrationCalculator.concentrationAt(nominalDoses, peakAt, halfLifeHours, weightKg)
            if (projected >= thresholds.focusLowerMgPerL) {
                at = at.plusMinutes(CaffeineConstants.SCAN_INTERVAL_MINUTES)
                continue
            }

            val chosen = options.firstNotNullOfOrNull { option ->
                fit(option, at, timeline, thresholds, halfLifeHours, weightKg, dailyLimitMg, totalMg, nominalDoses, latestDoses)
            }
            if (chosen == null) {
                at = at.plusMinutes(CaffeineConstants.SCAN_INTERVAL_MINUTES)
                continue
            }

            recommendations += chosen
            nominalDoses += TimedDose(chosen.startTime, chosen.caffeineMg.toDouble())
            latestDoses += TimedDose(chosen.endTime, chosen.caffeineMg.toDouble())
            totalMg += chosen.caffeineMg

            // 추천 구간만큼 건너뛰어 구간이 겹치지 않게 한다
            at = at.plusMinutes(CaffeineConstants.RECOMMENDATION_WINDOW_MINUTES)
        }

        return GoldenTime(recommendations, totalMg)
    }

    /* 청소년이 고른 에너지음료는 반 잔만 허용하고, 일반 음료를 먼저 검토한다.
    같은 우선순위 안에서는 카페인 함량이 높은 후보부터 검토한다. */
    private fun buildOptions(candidates: List<BeverageOption>, profile: BiometricProfile): List<Option> =
        candidates.flatMap { beverage ->
            val multipliers = if (profile.youth && beverage.energyDrink) listOf(0.5) else listOf(1.0, 0.5)
            multipliers.map { Option(beverage, it, (beverage.caffeineMg * it).roundToInt()) }
        }
            .filter { it.caffeineMg > 0 }
            .sortedWith(
                compareBy<Option> { if (profile.youth && it.beverage.energyDrink) 1 else 0 }
                    .thenByDescending { it.caffeineMg },
            )

    /* [at] 에 이 옵션을 마셔도 되는지 확인하고, 되면 구간 끝까지 정해 추천 한 건을 만든다.
    맞지 않으면 null. */
    private fun fit(
        option: Option,
        at: LocalDateTime,
        timeline: RhythmTimeline,
        thresholds: Thresholds,
        halfLifeHours: Double,
        weightKg: Double,
        dailyLimitMg: Int,
        totalMg: Int,
        nominalDoses: List<TimedDose>,
        latestDoses: List<TimedDose>,
    ): RecommendedIntake? {
        if (totalMg + option.caffeineMg > dailyLimitMg) return null

        val doseMg = option.caffeineMg.toDouble()
        val windowLimit = minOf(at.plusMinutes(CaffeineConstants.RECOMMENDATION_WINDOW_MINUTES), timeline.focusEnd)

        // 구간의 어느 시각에 마셔도 취침 상한을 지키도록 끝을 당긴다
        var end = maxOf(windowLimit, at)
        while (end.isAfter(at) && !sleepSafe(latestDoses, end, doseMg, timeline, thresholds, halfLifeHours, weightKg)) {
            end = end.minusMinutes(CaffeineConstants.SCAN_INTERVAL_MINUTES)
        }
        if (!sleepSafe(latestDoses, end, doseMg, timeline, thresholds, halfLifeHours, weightKg)) return null

        if (!upperSafe(nominalDoses, at, doseMg, timeline, thresholds, halfLifeHours, weightKg)) return null

        return RecommendedIntake(
            beverageId = option.beverage.id,
            beverageName = option.beverage.name,
            startTime = at,
            endTime = end,
            multiplier = option.multiplier,
            caffeineMg = option.caffeineMg,
        )
    }

    private fun sleepSafe(
        latestDoses: List<TimedDose>,
        at: LocalDateTime,
        doseMg: Double,
        timeline: RhythmTimeline,
        thresholds: Thresholds,
        halfLifeHours: Double,
        weightKg: Double,
    ): Boolean {
        val withNew = latestDoses + TimedDose(at, doseMg)
        val atSleep = ConcentrationCalculator.concentrationAt(withNew, timeline.sleep, halfLifeHours, weightKg)
        return atSleep <= thresholds.sleepUpperMgPerL
    }

    private fun upperSafe(
        nominalDoses: List<TimedDose>,
        at: LocalDateTime,
        doseMg: Double,
        timeline: RhythmTimeline,
        thresholds: Thresholds,
        halfLifeHours: Double,
        weightKg: Double,
    ): Boolean {
        val withNew = nominalDoses + TimedDose(at, doseMg)
        val peak = ConcentrationCalculator
            .curve(withNew, at, timeline.sleep, halfLifeHours, weightKg, CaffeineConstants.SCAN_INTERVAL_MINUTES)
            .maxOf { it.mgPerL }
        return peak <= thresholds.focusUpperMgPerL
    }
}
