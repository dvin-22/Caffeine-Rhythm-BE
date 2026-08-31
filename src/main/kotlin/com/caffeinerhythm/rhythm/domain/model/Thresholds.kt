package com.caffeinerhythm.rhythm.domain.model

/* 적정 농도 기준선 (mg/L). 셋의 의미가 각각 달라 배열이 아닌 독립적인 필드로 둔다. */
data class Thresholds(
    val focusLowerMgPerL: Double,
    val focusUpperMgPerL: Double,
    val sleepUpperMgPerL: Double,
)
