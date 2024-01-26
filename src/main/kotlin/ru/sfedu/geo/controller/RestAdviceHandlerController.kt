package ru.sfedu.geo.controller

import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RestAdviceHandlerController {

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNotFoundException(e: EntityNotFoundException) =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
}
