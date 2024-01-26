package ru.sfedu.geo.controller

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.sfedu.geo.dto.PlanDto
import ru.sfedu.geo.model.Plan
import ru.sfedu.geo.service.PlanService
import java.util.UUID

@Validated
@RequestMapping("/api/v1/plan")
@RestController
class PlanController(
    private val planService: PlanService
) {

    @GetMapping
    fun getPlans(pageable: Pageable): ResponseEntity<Page<Plan>> = ResponseEntity.ok(
        planService.getPlans(pageable)
    )

    @GetMapping("/{id}")
    fun getPlan(@PathVariable id: UUID): ResponseEntity<PlanDto> = ResponseEntity.ok(
        planService.getPlanDto(id)
    )

}
