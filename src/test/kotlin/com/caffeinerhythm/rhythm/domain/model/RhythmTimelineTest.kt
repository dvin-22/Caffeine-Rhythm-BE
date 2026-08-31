package com.caffeinerhythm.rhythm.domain.model

import com.caffeinerhythm.global.error.BusinessException
import com.caffeinerhythm.global.error.ErrorCode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RhythmTimelineTest {

    private val date = LocalDate.of(2026, 8, 28)

    private fun schedule(focusStart: String, focusEnd: String, sleep: String) = Schedule(
        focusStartTime = LocalTime.parse(focusStart),
        focusEndTime = LocalTime.parse(focusEnd),
        sleepTime = LocalTime.parse(sleep),
        beverageIds = listOf(1),
    )

    @Test
    fun `같은 날 안에서 끝나는 일정`() {
        val timeline = RhythmTimeline.of(date, schedule("10:00", "17:00", "23:00"))

        assertEquals(LocalDateTime.of(2026, 8, 28, 10, 0), timeline.focusStart)
        assertEquals(LocalDateTime.of(2026, 8, 28, 17, 0), timeline.focusEnd)
        assertEquals(LocalDateTime.of(2026, 8, 28, 23, 0), timeline.sleep)
    }

    @Test
    fun `자정을 넘기면 종료와 취침만 다음 날로 넘어간다`() {
        val timeline = RhythmTimeline.of(date, schedule("23:00", "06:00", "07:00"))

        assertEquals(LocalDateTime.of(2026, 8, 28, 23, 0), timeline.focusStart)
        assertEquals(LocalDateTime.of(2026, 8, 29, 6, 0), timeline.focusEnd)
        assertEquals(LocalDateTime.of(2026, 8, 29, 7, 0), timeline.sleep)
        assertEquals(date, timeline.rhythmDate)
    }

    @Test
    fun `취침 시각이 집중 구간 안에 있으면 거부한다`() {
        val exception = assertFailsWith<BusinessException> {
            RhythmTimeline.of(date, schedule("10:00", "17:00", "14:00"))
        }

        assertEquals(ErrorCode.SCHEDULE_OVERLAP, exception.errorCode)
    }

    @Test
    fun `자정을 넘긴 집중 구간 안에 취침이 있어도 거부한다`() {
        val exception = assertFailsWith<BusinessException> {
            RhythmTimeline.of(date, schedule("22:00", "05:00", "02:00"))
        }

        assertEquals(ErrorCode.SCHEDULE_OVERLAP, exception.errorCode)
    }
}
