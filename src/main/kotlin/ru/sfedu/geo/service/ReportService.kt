package ru.sfedu.geo.service

// import net.sf.jasperreports.engine.JasperCompileManager
// import net.sf.jasperreports.engine.JasperExportManager
// import net.sf.jasperreports.engine.JasperFillManager
// import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import ru.sfedu.geo.util.lazyLogger
import java.time.LocalDate
import java.util.Date
import java.util.UUID

@Service
class ReportService(
    private val planService: PlanService,
    private val orderService: OrderService,
    private val applicationContext: ApplicationContext,
) {
    private val log by lazyLogger()
    private val jrxml = "classpath:reports/report.jrxml"
    private val resource = applicationContext.getResource(jrxml)
    // private val jasperReport by lazy {
    //     resource.inputStream.use {
    //         JasperCompileManager.compileReport(it).also {
    //             log.debug("report compiled successfully")
    //         }
    //     }
    // }

    fun getReport(planId: UUID): ByteArray {
        // val plan = planService.getById(planId)
        // val orders = orderService.findByPlanId(planId)

        // val jasperPrint = JasperFillManager.fillReport(
        //     jasperReport,
        //     mapOf(
        //         "planId" to plan.id,
        //         "deliveryDate" to plan.deliveryDate.toDate()
        //     ),
        //     JRBeanCollectionDataSource(orders)
        // )
        // val byteArrayOutputStream = ByteArrayOutputStream()
        // JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);
        // return byteArrayOutputStream.toByteArray()

        return applicationContext
            .getResource("classpath:reports/report.pdf")
            .contentAsByteArray
    }

    private fun LocalDate.toDate(): Date = java.sql.Date.valueOf(this)
}
