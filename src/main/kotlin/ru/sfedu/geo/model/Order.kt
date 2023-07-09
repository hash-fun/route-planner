package ru.sfedu.geo.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.LocalDate
import java.util.UUID
import javax.annotation.processing.Generated

@Entity
@Table(
    name = "orders",
    uniqueConstraints = [
        UniqueConstraint(
            name = "order_ux",
            columnNames = ["id", "plan_id"]
        )
    ]
)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Order(
    @field:Id
    @field:Generated
    val id: UUID = UUID.randomUUID(),

    @field:Column(nullable = true, length = NAME_SIZE)
    val name: String? = null,

    @field:Column(nullable = false, length = ADDRESS_SIZE)
    val address: String? = null,

    @field:Column(name = "delivery_date", nullable = false)
    val deliveryDate: LocalDate? = null,

    @field:JsonIgnore
    @field:Column(name = "plan_id", nullable = false)
    val planId: UUID? = null,

    @field:Column(nullable = false)
    var number: Int? = null,
)
