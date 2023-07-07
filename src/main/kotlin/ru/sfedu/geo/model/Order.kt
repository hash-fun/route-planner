package ru.sfedu.geo.model

import java.util.UUID

data class Order(
    var id: UUID,
    var name: String,
    var address: String,
)
