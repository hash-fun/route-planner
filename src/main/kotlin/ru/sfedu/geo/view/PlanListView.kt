package ru.sfedu.geo.view

import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY
import com.vaadin.flow.component.button.ButtonVariant.LUMO_SUCCESS
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.dataview.GridListDataView
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import org.springframework.dao.DataIntegrityViolationException
import ru.sfedu.geo.model.Plan
import ru.sfedu.geo.service.PlanService
import ru.sfedu.geo.util.lazyLogger
import java.time.Clock
import java.time.LocalDate

@Route("")
class PlanListView(
    private val clock: Clock,
    private val planService: PlanService,
) : VerticalLayout() {
    private val log by lazyLogger()

    private val grid = Grid(Plan::class.java, false).apply {
        // columns
        addColumn(Plan::id).setHeader("ID")
        addColumn(Plan::deliveryDate).setHeader("Delivery Date")

        // events
        addItemDoubleClickListener {
            navigate(it.item)
        }
    }

    private val openPlanDialog = Dialog().apply {
        val datePicker = DatePicker("Date:")
        headerTitle = "Open Plan"
        add(datePicker)
        footer.add(Button("Cancel") { close() })
        footer.add(Button("Open") {
            val date = datePicker.value
            when (val plan = planService.findByDeliverDate(date)) {
                null -> Notification.show("Plan not found for date: $date").apply {
                    position = Notification.Position.BOTTOM_CENTER
                }.also {
                    log.debug("openPlan: plan not found for date={}", date)
                }

                else -> close().also {
                    navigate(plan)
                }
            }

        }.apply {
            addThemeVariants(LUMO_PRIMARY)
            addOpenedChangeListener {
                if (isOpened) {
                    datePicker.value = LocalDate.now(clock).plusDays(1)
                }
            }
        })
    }

    private val createPlanDialog = Dialog().apply {
        val datePicker = DatePicker("Date:")
        headerTitle = "Create Plan"
        add(datePicker)
        footer.add(Button("Cancel") { close() })
        footer.add(Button("Create") {
            val date = datePicker.value
            if (!datePicker.isEmpty && date != null) {
                when (val plan = try {
                    planService.createPlan(date).also {
                        dataView.addItem(it)
                    }
                } catch (e: DataIntegrityViolationException) {
                    log.debug("createPlan: error", e)
                    planService.findByDeliverDate(date)
                }) {
                    null -> Notification.show("Can't create or get existing plan for date: $date").apply {
                        position = Notification.Position.BOTTOM_CENTER
                    }.also {
                        log.debug("create: plan not found for date={}", date)
                    }

                    else -> close().also {
                        navigate(plan)
                    }
                }
            }
        }.apply {
            addThemeVariants(LUMO_PRIMARY)
            addOpenedChangeListener {
                if (isOpened) {
                    datePicker.value = LocalDate.now(clock).plusDays(1)
                }
            }
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

    private fun navigate(
        plan: Plan,
        block: () -> Unit = { log.debug("navigate: navigated to plan={}", plan) },
    ) = ui.ifPresent {
        it.navigate(PlanView::class.java, plan.id.toString())
    }.also {
        block()
    }
}
