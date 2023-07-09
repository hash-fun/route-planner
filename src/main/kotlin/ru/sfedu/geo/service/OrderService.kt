package ru.sfedu.geo.service

import org.springframework.stereotype.Service
import ru.sfedu.geo.model.Order
import ru.sfedu.geo.repository.OrderRepository
import java.util.UUID
import java.util.stream.Stream
import kotlin.streams.asSequence

@Service
class OrderService(
    private val orderRepository: OrderRepository,
) {
    fun findByPlanId(planId: UUID) =
        orderRepository.findByPlanIdOrderByNumber(planId)

    fun save(orderStream: Stream<Order>): List<Order> =
        orderStream.asSequence().mapIndexed { index, order ->
            order.apply {
                number = index + 1
            }
        }.toList().let {
            orderRepository.saveAll(it)
        }
}
