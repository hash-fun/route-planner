package ru.sfedu.geo.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.sfedu.geo.model.Order
import java.util.UUID

interface OrderRepository : JpaRepository<Order, UUID> {

    fun findByPlanIdOrderByNumber(planId: UUID): List<Order>
}
