package ru.sfedu.geo.service

import ru.sfedu.geo.model.Order
import java.time.LocalDate

interface ErpAdapter {
    fun fetchOrdersByDeliveryDate(deliveryDate: LocalDate): List<Order>
}
