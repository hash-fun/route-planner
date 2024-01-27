package ru.sfedu.geo.model

import jakarta.persistence.Embeddable

@Embeddable
data class Point(
    val lat: Double? = null,
    val long: Double? = null,
) {
    override fun toString(): String {
        return "($lat, $long)"
    }
}
