package ru.sfedu.geo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GeoApplication

fun main(args: Array<String>) {
    @Suppress("SpreadOperator")
    runApplication<GeoApplication>(*args)
}
