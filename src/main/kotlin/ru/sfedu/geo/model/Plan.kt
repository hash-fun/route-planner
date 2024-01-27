package ru.sfedu.geo.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDate
import java.util.UUID
import javax.annotation.processing.Generated

@Entity
data class Plan(
    @field:Id
    @field:Generated
    val id: UUID = UUID.randomUUID(),

    @field:Column(nullable = false, unique = true)
    val deliveryDate: LocalDate = LocalDate.now(),
)
