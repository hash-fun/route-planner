package ru.sfedu.geo.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
data class AppProperties(
    val daysBack: Int = 7,
    val daysForward: Int = 7,
)
