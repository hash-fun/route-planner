package ru.sfedu.geo.service

import ru.sfedu.geo.model.Point

fun interface PathFinder {
    fun distance(from: Point, to: Point): Double
}
