package ru.sfedu.geo.service.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import ru.sfedu.geo.AbstractComponentTest
import ru.sfedu.geo.service.GeoService

class GeoServiceTest : AbstractComponentTest() {

    @Autowired
    private lateinit var geoService: GeoService

    @Test
    fun `it should return expected coords`() {
        val actual = geoService.geocode("Некрасовский переулок, 44, Таганрог, Ростовская область")!!
        assertEquals(38.0, actual.lat!!, 1.0)
        assertEquals(47.0, actual.long!!, 1.0)
    }
}
