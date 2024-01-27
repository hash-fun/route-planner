package ru.sfedu.geo.service

import ru.sfedu.geo.model.Order
import ru.sfedu.geo.model.Point

fun interface TspSolver {
    fun solve(home: Point, orders: List<Order>): List<Order>?
}
