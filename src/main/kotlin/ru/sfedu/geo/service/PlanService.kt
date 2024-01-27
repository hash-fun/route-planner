package ru.sfedu.geo.service

import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.sfedu.geo.config.AppProperties
import ru.sfedu.geo.dto.PlanDto
import ru.sfedu.geo.model.Plan
import ru.sfedu.geo.repository.OrderRepository
import ru.sfedu.geo.repository.PlanRepository
import java.time.Clock
import java.time.LocalDate
import java.util.UUID

@Service
class PlanService(
    private val clock: Clock,
    private val appProperties: AppProperties,
    private val planRepository: PlanRepository,
    private val orderRepository: OrderRepository,
) {
    fun findByDeliverDate(deliveryDate: LocalDate) =
        planRepository.findByDeliveryDate(deliveryDate)

    fun findRecent(): List<Plan> = with(LocalDate.now(clock)) {
        planRepository.findByDeliveryDateBetween(
            minusDays(appProperties.daysBack.toLong()),
            plusDays(appProperties.daysForward.toLong())
        ).sortedBy {
            it.deliveryDate
        }
    }

    fun createPlan(date: LocalDate) =
        planRepository.save(
            Plan(
                id = UUID.randomUUID(),
                deliveryDate = date,
            )
        )

    fun getById(planId: UUID) = planRepository.getReferenceById(planId)

    fun getPlans(pageable: Pageable) =
        planRepository.findAll(pageable)

    @Transactional
    fun getPlanDto(id: UUID): PlanDto =
        planRepository.findById(id).map { plan ->
            PlanDto(
                plan.id,
                plan.deliveryDate,
                orderRepository.findByPlanIdOrderByNumber(id)
            )
        }.orElseThrow {
            EntityNotFoundException("not found")
        }

    fun save(plan: Plan) {
        planRepository.save(plan)
    }
}
