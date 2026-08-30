package com.caffeinerhythm.rhythm.domain.model

import java.time.LocalDateTime

/* 추천 섭취 1건. 구간으로 제시하되 농도 계산은 [startTime] 에 마신다고 가정한다.
[endTime] 은 그 시각에 마셔도 취침 상한을 넘지 않는 범위까지만 늘린다. */
data class RecommendedIntake(
    val beverageId: Int,
    val beverageName: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val multiplier: Double,
    val caffeineMg: Int,
)

/* 골든타임 역산 결과. 추천이 비어 있을 수 있다. */
data class GoldenTime(
    val recommendations: List<RecommendedIntake>,
    val totalCaffeineMg: Int,
) {
    val empty: Boolean get() = recommendations.isEmpty()

    companion object {
        val NONE = GoldenTime(emptyList(), 0)
    }
}
