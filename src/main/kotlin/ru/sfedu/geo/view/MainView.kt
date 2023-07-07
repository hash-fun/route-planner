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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import ru.sfedu.geo.model.Order
import ru.sfedu.geo.service.OrderService
import java.util.UUID


/**
 * The main view contains a button and a click listener.
 */
@Route("")
class MainView(
    private val orderService: OrderService,
) : VerticalLayout() {
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
        val name = TextField("Name");
        val address = TextField("Address");
        headerTitle = "New Order"
        add(VerticalLayout().apply {
            add(name, address)
        })
        footer.add(Button("Cancel") { close() })
        footer.add(Button("Save") {

            dataView.addItem(Order(UUID.randomUUID(), name.value, address.value))

            close()
        }.apply {
            addThemeVariants(LUMO_PRIMARY)
        })
    }

    private val getOrdersButton = Button("Get Orders") {
        dataView.addItems(orderService.getOrders())
    }.apply {
        addThemeVariants(LUMO_PRIMARY)
        addClickShortcut(Key.ENTER)
    }

    private val newOrder = Button("New Order") {
        newOrderDialog.open()
    }.apply {
        addThemeVariants(LUMO_PRIMARY, LUMO_SUCCESS)
    }

    private val pushButton = Button("Push Solution") {
        it.source.text = orderService.foo()
    }.apply {
        addThemeVariants(LUMO_PRIMARY, LUMO_ERROR)
    }


    private val buttonBar = HorizontalLayout().apply {
        add(getOrdersButton)
        add(newOrder)
        add(pushButton)
    }

    private val dataView: GridListDataView<Order> = grid.setItems(mutableListOf())

    init {
        // Use custom CSS classes to apply styling. This is defined in shared-styles.css.
        addClassName("centered-content")
        add(
            grid,
            newOrderDialog,
            buttonBar
        )
    }
}
