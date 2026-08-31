package com.caffeinerhythm.rhythm.domain.model

import com.caffeinerhythm.global.error.BusinessException
import com.caffeinerhythm.global.error.ErrorCode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/* 하루 일정. 시각은 날짜가 없는 시간대 기준이고, 종료가 시작보다 이르면 다음 날로 해석한다.
실제 절대 시각 해석은 [RhythmTimeline] 이 맡는다. */
data class Schedule(
    val focusStartTime: LocalTime,
    val focusEndTime: LocalTime,
    val sleepTime: LocalTime,
    val beverageIds: List<Int>,
)

/* 날짜가 없는 시간대를 절대 시각으로 확정한다. 하루는 집중 시작 시각부터 시작하므로,
집중 종료와 취침이 집중 시작보다 이르면 다음 날로 넘긴다. */
data class RhythmTimeline(
    val rhythmDate: LocalDate,
    val focusStart: LocalDateTime,
    val focusEnd: LocalDateTime,
    val sleep: LocalDateTime,
) {
    companion object {
        fun of(rhythmDate: LocalDate, schedule: Schedule): RhythmTimeline {
            val focusStart = LocalDateTime.of(rhythmDate, schedule.focusStartTime)
            val focusEnd = firstAfter(focusStart, schedule.focusEndTime)
            val sleep = firstAfter(focusStart, schedule.sleepTime)

            // 취침이 집중 구간 안에 들어오는 경우
            if (sleep.isBefore(focusEnd)) throw BusinessException(ErrorCode.SCHEDULE_OVERLAP)

            return RhythmTimeline(rhythmDate, focusStart, focusEnd, sleep)
        }

        private fun firstAfter(from: LocalDateTime, time: LocalTime): LocalDateTime {
            val sameDay = LocalDateTime.of(from.toLocalDate(), time)
            return if (sameDay.isAfter(from)) sameDay else sameDay.plusDays(1)
        }
    }
}
