package ru.sfedu.geo.service.impl

import com.jayway.jsonpath.JsonPath
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import ru.sfedu.geo.config.GeoCacheConfig.Companion.GEO_CACHE
import ru.sfedu.geo.model.Point
import ru.sfedu.geo.repository.YandexGeocodeRepository
import ru.sfedu.geo.service.GeoService
import ru.sfedu.geo.util.lazyLogger

@Service
class YandexGeoService(
    private val yandexGeocodeRepository: YandexGeocodeRepository,
) : GeoService {
    private val log by lazyLogger()

    @Cacheable(cacheNames = [GEO_CACHE], key = "#address")
    override fun geocode(address: String) = runCatching {
        log.debug("geocode: {}", address)
        val json = yandexGeocodeRepository.geocode(address)
        log.debug("result: {}", json)
        val value = JsonPath.parse(json).read<List<String>>(JSON_PATH)?.firstOrNull()
        log.debug("value: {}", value)
        val arr = value.toString().split(' ')
        Point(arr[1].toDouble(), arr[0].toDouble())
    }.onFailure {
        log.error("error: ", it)
    }.getOrNull()

    companion object {
        const val JSON_PATH = "\$..Point.pos"
    }
}
