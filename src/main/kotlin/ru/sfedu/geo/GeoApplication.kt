package ru.sfedu.geo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


@EnableFeignClients
@EnableJpaRepositories
@SpringBootApplication
class GeoApplication : SpringBootServletInitializer() {
    override fun configure(
        application: SpringApplicationBuilder,
    ): SpringApplicationBuilder? {
        return application.sources(GeoApplication::class.java)
    }
}

fun main(args: Array<String>) {
    @Suppress("SpreadOperator")
    runApplication<GeoApplication>(*args)
}
