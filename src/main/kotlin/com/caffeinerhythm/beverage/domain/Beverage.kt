package com.caffeinerhythm.beverage.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

// 음료 마스터. 시드로만 채우고 앱에는 쓰기 API 를 두지 않는다.
@Entity
@Table(name = "beverages")
class Beverage(

    @Column(name = "name", nullable = false, length = 50, unique = true)
    val name: String,

    @Column(name = "energy_drink", nullable = false)
    val energyDrink: Boolean,

    @Column(name = "serving_size_ml", nullable = false)
    val servingSizeMl: Short,

    @Column(name = "caffeine_mg", nullable = false)
    val caffeineMg: Short,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int = 0,
)
