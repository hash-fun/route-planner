package ru.sfedu.geo.service.impl

import org.springframework.stereotype.Service
import ru.sfedu.geo.model.Order
import ru.sfedu.geo.service.ErpAdapter
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import kotlin.random.Random

@Service
class ErpAdapterMock(
    clock: Clock,
) : ErpAdapter {

    private val dateOrdersMap = mockDateOrdersMap(LocalDate.now(clock))

    override fun fetchOrdersByDeliveryDate(deliveryDate: LocalDate) =
        dateOrdersMap[deliveryDate] ?: listOf()

    @Suppress("MagicNumber")
    companion object {
        private val addresses = listOf(
            "Ленина, 175, Таганрог,",
            "Восточная, 4, Таганрог,",
            "Жукаова, 2, Таганрог",
            "Авиаторов 4, Таганрог, ",
            "Ленина, 448, Покровсекое, Ростовская область",
            "Советская улица, 24, Новошахтинск, Ростовская область",
            "Советская улица, 204, Шахты, Ростовская область",
            "улица Ворошилова, 250, город Морозовск, Ростовская область",
            "Октябрьская улица, 244А, Калач-на-Дону, Волгоградская область",
            "проспект Курчатова, 4Б, Волгодонск, Ростовская область",
            "Советская улица, 11, село Летняя Ставка, Туркменский муниципальный округ, Ставропольский край",
            "Красноармейская улица, 50, Тихорецк, Краснодарский край, 352120"
        )

        private val random = Random(Instant.now().epochSecond)

        private fun mockDateOrdersMap(now: LocalDate) = (1..7)
            .asSequence()
            .map { now.plusDays(it.toLong()) }
            .map { it to mockOrders(it) }
            .toMap()

        private fun mockOrders(date: LocalDate) = (1..12)
            .asSequence()
            .map { addresses[random.nextInt(addresses.size)] }
            .map {
                Order(
                    id = UUID.randomUUID(),
                    name = "Зкакз №${random.nextInt(100500)}",
                    address = it,
                    deliveryDate = date
                )
            }
            .toList()


    }
}
