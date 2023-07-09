package ru.sfedu.geo.view

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
import ru.sfedu.geo.model.Order
import ru.sfedu.geo.service.ErpAdapter
import ru.sfedu.geo.service.OrderService
import ru.sfedu.geo.service.PlanService
import ru.sfedu.geo.util.lazyLogger
import java.time.LocalDate
import java.util.UUID
import kotlin.streams.asSequence


@Route(value = "plan")
class PlanView(
    private val planService: PlanService,
    private val orderService: OrderService,
    private val erpAdapter: ErpAdapter,
) : VerticalLayout(), HasUrlParameter<String> {
    private val log by lazyLogger()

    private lateinit var planId: UUID
    private lateinit var deliveryDate: LocalDate
    private lateinit var dataView: GridListDataView<Order>

    private var draggedItem: Order? = null

    private val grid = Grid(Order::class.java, false).apply {
        // columns
        addColumn(Order::id).setHeader("ID")
        addColumn(Order::name).setHeader("Name")
        addColumn(Order::address).setHeader("Address")

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
                }
            }
        }

        addDragEndListener {
            draggedItem = null
        }
    }

    private val newOrderDialog = Dialog().apply {
        val nameTextField = TextField("Name")
        val addressTextField = TextField("Address")
        headerTitle = "New Order"
        add(VerticalLayout().apply {
            add(nameTextField, addressTextField)
        })
        footer.add(Button("Cancel") { close() })
        footer.add(Button("Save") {

            dataView.addItem(
                Order(
                    name = nameTextField.value,
                    address = addressTextField.value,
                    planId = planId
                )
            )

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

    private val getOrdersButton = Button("Get Orders") {
        val newOrders = erpAdapter.fetchOrdersByDeliveryDate(deliveryDate)
        val ids = dataView.items.asSequence().map(Order::id).toSet()
        val filtered = newOrders.filterNot { it.id in ids }.map { it.copy(planId = planId) }
        when {
            filtered.isEmpty() -> Notification.show("Новых заказоа нет").apply {
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

    private val newOrder = Button("New Order") {
        newOrderDialog.open()
    }.apply {
        addThemeVariants(LUMO_PRIMARY, LUMO_SUCCESS)
    }

    private val saveButton = Button("Save") {
        orderService.save(dataView.items).sortedBy { it.number }.let {
            dataView = grid.setItems(it)
        }
    }.apply {
        addThemeVariants(LUMO_PRIMARY, LUMO_ERROR)
    }

    private val buttonBar = HorizontalLayout().apply {
        add(getOrdersButton)
        add(newOrder)
        add(saveButton)
    }

    init {
        addClassName("centered-content")
        add(
            grid,
            newOrderDialog,
            buttonBar
        )
    }

    override fun setParameter(event: BeforeEvent, parameter: String) {
        log.debug("setParameter: event: {}, parameter: {}", event, parameter)
        planId = UUID.fromString(parameter)
        orderService.findByPlanId(planId).toMutableList().let {
            dataView = grid.setItems(it)
        }
        deliveryDate = planService.getById(planId).deliveryDate
    }
}
