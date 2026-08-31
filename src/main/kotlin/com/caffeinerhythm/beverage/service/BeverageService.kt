package com.caffeinerhythm.beverage.service

import com.caffeinerhythm.beverage.domain.Beverage
import com.caffeinerhythm.beverage.repository.BeverageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BeverageService(
    private val beverages: BeverageRepository,
) {

    @Transactional(readOnly = true)
    // 저장된 음료를 ID 오름차순으로 조회한다.
    fun list(): List<Beverage> = beverages.findAllByOrderByIdAsc()
}
