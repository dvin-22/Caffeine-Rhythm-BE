package com.caffeinerhythm.rhythm.domain.model

/* 카페인 추천 계산에 필요한 음료 정보만 담은 후보 모델이다.
음료 마스터 엔티티와 계산 로직을 분리하기 위해 사용한다. */
data class BeverageOption(
    val id: Int,
    val name: String,
    val caffeineMg: Int,
    val energyDrink: Boolean,
)
