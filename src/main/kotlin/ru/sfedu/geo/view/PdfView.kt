package ru.sfedu.geo.view

import com.vaadin.componentfactory.pdfviewer.PdfViewer
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.router.BeforeEvent
import com.vaadin.flow.router.HasUrlParameter
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import ru.sfedu.geo.service.ReportService
import ru.sfedu.geo.util.lazyLogger
import java.util.UUID


@Route(value = "pdf")
class PdfView(
    private val reportService: ReportService
) : Div(), HasUrlParameter<String> {
    private val log by lazyLogger()
    private val pdfViewer = PdfViewer().apply {
        setSizeFull()
    }

    init {
        add(pdfViewer)
    }

    override fun setParameter(event: BeforeEvent, parameter: String) {
        log.debug("setParameter: parameter: {}", parameter)
        val planId = UUID.fromString(parameter)
        val bytes = reportService.getReport(planId)

        val resource = StreamResource("report.pdf", InputStreamFactory {
            bytes.inputStream()
        })
        pdfViewer.setSrc(resource)
    }
}
