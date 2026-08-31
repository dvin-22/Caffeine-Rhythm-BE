package com.caffeinerhythm.rhythm.domain.model

import java.time.LocalDateTime

data class ConcentrationPoint(
    val at: LocalDateTime,
    val mgPerL: Double,
)

/* 특정 시각에 마신 카페인 한 건. 계산기 내부에서만 쓴다. */
data class TimedDose(
    val at: LocalDateTime,
    val doseMg: Double,
)
