package ru.sfedu.geo.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.sfedu.geo.model.Plan
import java.time.LocalDate
import java.util.UUID

interface PlanRepository : JpaRepository<Plan, UUID> {
    fun findByDeliveryDate(deliveryDate: LocalDate): Plan?

    fun findByDeliveryDateBetween(from: LocalDate, to: LocalDate): List<Plan>
}
