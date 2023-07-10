package ru.sfedu.geo.repository

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    value = "yandex-geocoding",
    url = "\${app.yandex.geocoding.url}"
)
interface YandexGeocodeRepository {

    @GetMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun geocode(
        @RequestParam(GEOCODE_PARAMETER) address: String,
        @RequestParam(APIKEY_PARAMETER) apiKey: String = KEY,
        @RequestParam(LAT_LONG_PARAMETER) sco: String = LAT_LONG,
        @RequestParam(FORMAT_PARAMETER) format: String = JSON,
    ): String

    companion object {
        private const val GEOCODE_PARAMETER = "geocode"
        private const val APIKEY_PARAMETER = "apikey"
        private const val FORMAT_PARAMETER = "format"
        private const val LAT_LONG_PARAMETER = "sco"

        private const val LAT_LONG = "latlong"
        private const val JSON = "json"
        private const val KEY = "6fd32c30-b88a-49d0-a44b-d9550322118a"
    }
}
