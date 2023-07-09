package ru.sfedu.geo.service

import org.springframework.stereotype.Service
import ru.sfedu.geo.model.Order
import java.time.Instant

@Service
class OrderService {
    fun foo(): String = Instant.now().toString()

    fun getOrders() = (1..2).map {
        Order(
            name = "Test Order # $it",
            address = "Москва, Кремль, $it"
        )
    }
}
