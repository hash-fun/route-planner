package ru.sfedu.geo.service.impl

import org.springframework.stereotype.Component
import ru.sfedu.geo.model.Point
import ru.sfedu.geo.service.PathFinder
import kotlin.math.pow
import kotlin.math.sqrt


@Component
class PathFinderImpl : PathFinder {
    override fun distance(from: Point, to: Point) = sqrt(
        ((to.lat!! - from.lat!!) * FI_FACTOR).pow(2) + ((to.long!! - from.long!!) * FI_FACTOR).pow(2)
    )

    companion object {
        private const val FI_FACTOR = 1000
    }
}
