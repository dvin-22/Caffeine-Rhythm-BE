package com.caffeinerhythm.rhythm.domain.calculator

/* 계산에 쓰는 모든 상수이다. */
object CaffeineConstants {

    // 농도 계산 상수
    const val BIOAVAILABILITY = 0.99
    const val VOLUME_OF_DISTRIBUTION_PER_KG = 0.6
    const val ABSORPTION_RATE_PER_HOUR = 4.9

    // 생체 정보 미입력 시 계산에만 적용하는 표준값 (30세 / 70kg / 보통)
    const val DEFAULT_AGE_YEARS = 30
    const val DEFAULT_WEIGHT_KG = 70.0
    const val DEFAULT_SENSITIVITY_LEVEL = 3

    // 성인 표준 70kg 기준 농도 기준선. 민감도 계수 k를 곱해서 쓴다.
    const val FOCUS_LOWER_BASE_MG_PER_L = 1.4
    const val FOCUS_UPPER_BASE_MG_PER_L = 4.7
    const val SLEEP_UPPER_BASE_MG_PER_L = 1.4

    // 하루 총량 상한
    const val ADULT_DAILY_LIMIT_MG = 400
    const val YOUTH_DAILY_LIMIT_MG_PER_KG = 2.5

    /* 체중을 입력하지 않은 청소년의 상한 계산에만 쓴다. Vd 계산에는 쓰지 않는다. */
    const val YOUTH_FALLBACK_WEIGHT_KG = 55.0

    val YOUTH_AGES = 14..18

    // 골든타임 역산
    const val FIRST_INTAKE_LEAD_MINUTES = 60L
    const val SCAN_INTERVAL_MINUTES = 10L
    const val PEAK_DELAY_MINUTES = 45L

    /* 추천 구간의 길이이자 추천 사이의 최소 간격. 같은 값이라 구간이 서로 겹치지 않는다. */
    const val RECOMMENDATION_WINDOW_MINUTES = 120L

    /* 하루 총량 안에 들어와도 대여섯 잔을 늘어놓는 것은 조언이 아니다. */
    const val MAX_RECOMMENDATIONS = 4

    // 예상 농도 곡선
    const val CURVE_INTERVAL_MINUTES = 10L
    const val CURVE_MARGIN_HOURS = 3L
}
