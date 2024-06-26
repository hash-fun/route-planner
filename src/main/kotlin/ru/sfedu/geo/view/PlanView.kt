package ru.sfedu.geo.view

import com.flowingcode.vaadin.addons.googlemaps.GoogleMap
import com.flowingcode.vaadin.addons.googlemaps.GoogleMap.MapType.ROADMAP
import com.flowingcode.vaadin.addons.googlemaps.GoogleMapMarker
import com.flowingcode.vaadin.addons.googlemaps.GoogleMapPoint
import com.flowingcode.vaadin.addons.googlemaps.GoogleMapPolygon
import com.flowingcode.vaadin.addons.googlemaps.LatLon
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant.LUMO_ERROR
import com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY
import com.vaadin.flow.component.button.ButtonVariant.LUMO_SUCCESS
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.dataview.GridListDataView
import com.vaadin.flow.component.grid.dnd.GridDropLocation
import com.vaadin.flow.component.grid.dnd.GridDropMode
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.BeforeEvent
import com.vaadin.flow.router.HasUrlParameter
import com.vaadin.flow.router.Route
import org.springframework.beans.factory.annotation.Value
import ru.sfedu.geo.model.Order
import ru.sfedu.geo.model.Plan
import ru.sfedu.geo.model.Point
import ru.sfedu.geo.service.ErpAdapter
import ru.sfedu.geo.service.GeoService
import ru.sfedu.geo.service.OrderService
import ru.sfedu.geo.service.PlanService
import ru.sfedu.geo.service.VrpSolver
import ru.sfedu.geo.util.lazyLogger
import java.time.LocalDate
import java.util.UUID
import kotlin.streams.asSequence


@Suppress("LongParameterList")
@Route(value = "plan")
class PlanView(
    private val planService: PlanService,
    private val orderService: OrderService,
    private val erpAdapter: ErpAdapter,
    private val geoService: GeoService,
    private val vrpSolver: VrpSolver,
    @Value("\${app.google.api-key}")
    private val apiKey: String,
    @Value("\${app.home}")
    private val appHome: String,
) : VerticalLayout(), HasUrlParameter<String> {
    private val log by lazyLogger()

    private lateinit var plan: Plan
    private lateinit var planId: UUID
    private lateinit var deliveryDate: LocalDate
    private lateinit var dataView: GridListDataView<Order>

    private var draggedItem: Order? = null

    private val googleMap = GoogleMap(apiKey, null, null).apply {
        mapType = ROADMAP
        // center = LatLon(47.203821, 38.944089)
        val (lat, lon) = appHome.toLatLon()
        center = LatLon(lat, lon)
        width = "100%"
        height = "400px"
    }

    private val googleMapHome = appHome.toLatLon().run { GoogleMapPoint(this[0], this[1]) }
    private val googleMapMarkers = mutableSetOf<GoogleMapMarker>()
    private var googleMapPolygon: GoogleMapPolygon? = null

    private val grid = Grid(Order::class.java, false).apply {
        // columns
        addColumn(Order::id).setHeader("ID")
        addColumn(Order::name).setHeader("Номер")
        addColumn(Order::address).setHeader("Адрес")
        addColumn(Order::point).setHeader("Координаты")

        // drag-n-drop
        dropMode = GridDropMode.BETWEEN
        isRowsDraggable = true

        addDragStartListener {
            draggedItem = it.draggedItems.firstOrNull()
        }

        addDropListener {
            it.dropTargetItem.ifPresent { targetOrder ->
                if (draggedItem != targetOrder) {
                    dataView.removeItem(draggedItem)
                    if (it.dropLocation == GridDropLocation.BELOW) {
                        dataView.addItemAfter(draggedItem, targetOrder)
                    } else {
                        dataView.addItemBefore(draggedItem, targetOrder)
                    }
                    refreshRoute()
                }
            }
        }

        addDragEndListener {
            draggedItem = null
        }
    }

    private val newOrderDialog = Dialog().apply {
        val nameTextField = TextField("Номер/Название")
        val addressTextField = TextField("Адрес")
        headerTitle = "Новый заказ"
        add(VerticalLayout().apply {
            add(nameTextField, addressTextField)
        })
        footer.add(Button("Cancel") { close() })
        footer.add(Button("Save") {

            dataView.addItem(
                Order(
                    name = nameTextField.value,
                    address = addressTextField.value,
                    deliveryDate = plan.deliveryDate,
                    planId = planId
                )
            )
            plan.routed = false
            refreshRoute()

            close()
        }.apply {
            addThemeVariants(LUMO_PRIMARY)
            addOpenedChangeListener {
                if (isOpened) {
                    nameTextField.clear()
                    addressTextField.clear()
                }
            }
        })
    }

    private val getOrdersButton = Button("Получить Заказы") {
        val newOrders = erpAdapter.fetchOrdersByDeliveryDate(deliveryDate)
        val ids = dataView.items.asSequence().map(Order::id).toSet()
        val filtered = newOrders.filterNot { it.id in ids }.map { it.copy(planId = planId) }
        when {
            filtered.isEmpty() -> Notification.show("Новых заказов нет").apply {
                position = Notification.Position.BOTTOM_CENTER
            }.also {
                log.debug("fetchOrders: no new orders fetched")
            }

            else -> dataView.addItems(filtered).also {
                log.debug("fetchOrders: ")
                Notification.show("${filtered.size} новых заказов получено").apply {
                    position = Notification.Position.BOTTOM_CENTER
                }.also {
                    log.debug("fetchOrders: new orders fetched={}", filtered)
                }

            }
        }

    }.apply {
        addThemeVariants(LUMO_PRIMARY)
        addClickShortcut(Key.ENTER)
    }

    private val newOrder = Button("Новый Заказ") {
        newOrderDialog.open()
    }.apply {
        addThemeVariants(LUMO_PRIMARY, LUMO_SUCCESS)
    }

    private val saveButton = Button("Сохранить") {
        planService.save(plan)
        orderService.save(dataView.items).sortedBy { it.number }.let {
            dataView = grid.setItems(it)
        }
    }.apply {
        addThemeVariants(LUMO_PRIMARY, LUMO_ERROR)
    }

    private val buildRouteButton = Button("Построить маршрут") {
        // locations
        dataView.items.forEach { order ->
            order.address.takeIf { !it.isNullOrBlank() }
                ?.let { geoService.geocode(it) }
                ?.let { order.point = it }
        }
        dataView.refreshAll()

        // route
        val home = appHome.toLatLon().let { (lat, lon) -> Point(lat, lon) }
        val orders = dataView.items.toList()
        when (val solution = vrpSolver.solve(home, orders)) {
            null -> {
                plan.routed = false
                Notification.show("Маршрут не найден")
            }

            else -> {
                solution.forEachIndexed { i, order -> order.number = i.inc() }
                dataView.removeItems(solution)
                dataView.addItems(solution)
                plan.routed = true
            }
        }
        refreshMarkers()
        refreshRoute()
    }.apply {
        addThemeVariants(LUMO_PRIMARY)
    }

    private val buttonBar = HorizontalLayout().apply {
        add(
            getOrdersButton,
            newOrder,
            buildRouteButton,
            saveButton,
        )
    }

    init {
        log.debug("app.home: {}", appHome)
        addClassName("centered-content")
        add(
            googleMap,
            grid,
            newOrderDialog,
            buttonBar,
        )
    }

    override fun setParameter(event: BeforeEvent, parameter: String) {
        log.debug("setParameter: event: {}, parameter: {}", event, parameter)
        planId = UUID.fromString(parameter)
        plan = planService.getById(planId)
        deliveryDate = plan.deliveryDate
        orderService.findByPlanId(planId).toMutableList().let {
            dataView = grid.setItems(it)
            refreshMarkers()
            refreshRoute()
        }
    }

    private fun refreshMarkers() {
        // remove old
        googleMapMarkers.forEach(googleMap::removeMarker)
        googleMapMarkers.clear()

        // add new
        dataView.items.forEach {
            it.point?.toLatLon()?.run {
                val googleMapMarker = GoogleMapMarker(it.name, this, false)
                googleMapMarkers.add(googleMapMarker)
                googleMap.addMarker(googleMapMarker)
            }
        }
    }

    private fun refreshRoute() {
        googleMapPolygon?.takeIf { it.parent == googleMap }?.run { googleMap.removePolygon(this) }
        if (plan.routed) {
            googleMapPolygon = dataView.items.map { it.point }.toList().filterNotNull().map {
                GoogleMapPoint(it.lat, it.long)
            }.plus(googleMapHome).let {
                GoogleMapPolygon(it).apply {
                    fillOpacity = 0.0
                    strokeColor = "red"
                }
            }
            googleMap.addPolygon(googleMapPolygon)
        }
    }

    private fun Point.toLatLon(): LatLon? = when {
        lat != null && long != null -> LatLon(lat, long)
        else -> null
    }

    companion object {
        private fun String.toLatLon() = split(',').map { it.toDouble() }

    }
}
