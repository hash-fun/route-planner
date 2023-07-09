package ru.sfedu.geo.view

import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY
import com.vaadin.flow.component.button.ButtonVariant.LUMO_SUCCESS
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.dataview.GridListDataView
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import ru.sfedu.geo.model.Plan
import ru.sfedu.geo.service.PlanService
import ru.sfedu.geo.util.lazyLogger

@Route("")
class PlanListView(
    private val planService: PlanService,
) : VerticalLayout() {
    private val log by lazyLogger()

    private val grid = Grid(Plan::class.java, false).apply {
        // columns
        addColumn(Plan::id).setHeader("ID")
        addColumn(Plan::deliveryDate).setHeader("Delivery Date")
        addColumn(Plan::ordersCount).setHeader("Order Count")
    }

    private val openPlanDialog = Dialog().apply {
        val datePicker = DatePicker("Date:")
        headerTitle = "Open Plan"
        add(datePicker)
        footer.add(Button("Cancel") { close() })
        footer.add(Button("Open") {
            val date = datePicker.value
            when(val plan = planService.findByDeliverDate(date)) {
                null -> log.debug("openPlan: plan not found for date={}", date)
                else -> close().also {
                    // navigate
                    log.debug("openPlan: navigating to plan id={} for date={}", plan.id, plan.deliveryDate)
                }
            }

        }.apply {
            addThemeVariants(LUMO_PRIMARY)
        })
    }

    private val createPlanDialog = Dialog().apply {
        val date = DatePicker("Date:")
        headerTitle = "Create Plan"
        add(date)
        footer.add(Button("Cancel") { close() })
        footer.add(Button("Create") {
            close()
            // todo navigate
        }.apply {
            addThemeVariants(LUMO_PRIMARY)
        })
    }

    private val openPlanButton = Button("Open Plan ...") {
        openPlanDialog.open()
    }.apply {
        addThemeVariants(LUMO_PRIMARY)
        addClickShortcut(Key.ENTER)
    }

    private val createPlanButton = Button("Create Plan ...") {
        createPlanDialog.open()
    }.apply {
        addThemeVariants(LUMO_PRIMARY, LUMO_SUCCESS)
    }

    private val buttonBar = HorizontalLayout().apply {
        add(openPlanButton, createPlanButton)
    }

    private val dataView: GridListDataView<Plan> =
        grid.setItems(planService.findRecent())


    init {
        add(
            grid,
            openPlanDialog,
            createPlanDialog,
            buttonBar
        )
    }
}
