package com.caffeinerhythm.beverage.repository

import com.caffeinerhythm.beverage.domain.Beverage
import org.springframework.data.jpa.repository.JpaRepository

interface BeverageRepository : JpaRepository<Beverage, Int> {
    fun findAllByOrderByIdAsc(): List<Beverage>
}
