package ru.sfedu.geo.model

import jakarta.persistence.Embeddable

@Embeddable
data class Point(
    val lat: Float? = null,
    val long: Float? = null,
) {
    override fun toString(): String {
        return "($lat, $long)"
    }
}
