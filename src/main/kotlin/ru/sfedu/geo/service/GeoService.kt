package ru.sfedu.geo.service

import ru.sfedu.geo.model.Point

fun interface GeoService {
    fun geocode(address: String): Point?
}
