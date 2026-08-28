package com.caffeinerhythm.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock
import java.time.ZoneId

@Configuration
class ClockConfig {

    /**
     * 사용자에게 보이는 시각과 리듬 일자 판정은 Asia/Seoul 기준이다.
     * 테스트에서 고정 시각을 주입할 수 있도록 직접 호출 대신 이 빈을 쓴다.
     */
    @Bean
    fun clock(): Clock = Clock.system(SERVICE_ZONE)

    companion object {
        val SERVICE_ZONE: ZoneId = ZoneId.of("Asia/Seoul")
    }
}
