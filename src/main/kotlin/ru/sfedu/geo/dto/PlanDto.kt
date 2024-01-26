package ru.sfedu.geo.dto

import ru.sfedu.geo.model.Order
import java.time.LocalDate
import java.util.UUID

data class PlanDto(
    val id: UUID,
    val deliveryDate: LocalDate,
    val orders: List<Order>
)

