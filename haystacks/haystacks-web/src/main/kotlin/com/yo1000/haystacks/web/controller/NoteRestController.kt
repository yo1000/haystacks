package com.yo1000.haystacks.web.controller

import com.yo1000.haystacks.web.service.TableApplicationService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

/**
 *
 * @author yo1000
 */
@RequestMapping("/api/notes")
open class NoteRestController(
        private val tableApplicationService: TableApplicationService
) {
    @PostMapping("/{fullyQualifiedName}")
    fun post(@PathVariable fullyQualifiedName: String, @RequestBody(required = false) note: String?) =
            tableApplicationService.setNote(fullyQualifiedName, note ?: "")
}
