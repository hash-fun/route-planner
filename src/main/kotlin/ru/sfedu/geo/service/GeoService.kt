package ru.sfedu.geo.service

import ru.sfedu.geo.model.Point

interface GeoService {
    fun geocode(address: String): Point?
}
