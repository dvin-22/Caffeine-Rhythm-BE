package com.caffeinerhythm.beverage.controller

import com.caffeinerhythm.beverage.dto.BeverageResponseDto
import com.caffeinerhythm.beverage.service.BeverageService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// 음료 마스터 목록. 비회원 체험 화면에서도 쓰므로 인증하지 않는다.
@RestController
@RequestMapping("/api/v1/beverages")
class BeverageController(
    private val beverageService: BeverageService,
) {

    @GetMapping
    // 음료 목록을 조회해 API 응답 DTO 목록으로 반환한다.
    fun list(): List<BeverageResponseDto> = beverageService.list().map(BeverageResponseDto::from)
}
