package ru.sfedu.geo.service

import org.springframework.stereotype.Service
import ru.sfedu.geo.model.Order
import java.time.Instant
import java.util.UUID

@Service
class OrderService {
    fun foo(): String = Instant.now().toString()

    fun getOrders() = (1..2).map {
        Order(
            UUID.randomUUID(), "Test Order # $it", "Москва, Кремль, $it"
        )
    }
}
