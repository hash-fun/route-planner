package ru.sfedu.geo.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@EnableCaching
@Configuration
class GeoCacheConfig {

    @Bean("eventNotification")
    fun geoCacheManager(): CacheManager {
        return CaffeineCacheManager(GEO_CACHE).apply {
            setCaffeine(
                Caffeine.newBuilder().apply {
                    initialCapacity(1024)
                    maximumSize(Int.MAX_VALUE.toLong())
                    recordStats()
                }
            )
        }
    }

    companion object {
        const val GEO_CACHE = "geoCache"
    }
}
