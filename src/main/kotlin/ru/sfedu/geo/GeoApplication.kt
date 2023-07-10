package ru.sfedu.geo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableFeignClients
@EnableJpaRepositories
@SpringBootApplication
class GeoApplication

fun main(args: Array<String>) {
    @Suppress("SpreadOperator")
    runApplication<GeoApplication>(*args)
}
