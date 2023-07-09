package ru.sfedu.geo.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Transient
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.time.LocalDate
import java.util.UUID
import javax.annotation.processing.Generated

@Entity
data class Plan(
    @field:Id
    @field:Generated
    val id: UUID = UUID.randomUUID(),

    @field:Column(nullable = false, unique = true)
    val deliveryDate: LocalDate,

    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "plan_id", nullable = false, insertable = false, updatable = false)
    @Fetch(FetchMode.SUBSELECT)
    val orders: List<Order>,
) {
    @Transient
    fun ordersCount() = orders.count()
}
