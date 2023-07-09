package ru.sfedu.geo.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.UUID
import javax.annotation.processing.Generated

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Order(
    @field:Id
    @field:Generated
    val id: UUID = UUID.randomUUID(),

    @field:Column(nullable = true, length = NAME_SIZE)
    val name: String? = null,

    @field:Column(nullable = false, length = ADDRESS_SIZE)
    val address: String,

    @field:JsonIgnore
    @field:Column(name = "plan_id", nullable = false)
    val planId: UUID? = null,
)
