package com.caffeinerhythm.beverage.dto

import com.caffeinerhythm.beverage.domain.Beverage

data class BeverageResponseDto(
    val id: Int,
    val name: String,
    val energyDrink: Boolean,
    val servingSizeMl: Int,
    val caffeineMg: Int,
) {
    companion object {
        fun from(beverage: Beverage) = BeverageResponseDto(
            id = beverage.id,
            name = beverage.name,
            energyDrink = beverage.energyDrink,
            servingSizeMl = beverage.servingSizeMl.toInt(),
            caffeineMg = beverage.caffeineMg.toInt(),
        )
    }
}
