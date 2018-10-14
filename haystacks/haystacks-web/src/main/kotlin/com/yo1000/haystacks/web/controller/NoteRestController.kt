package com.yo1000.haystacks.web.controller

import com.yo1000.haystacks.core.service.TableService
import com.yo1000.haystacks.core.valueobject.FullyQualifiedName
import com.yo1000.haystacks.core.valueobject.Note
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
        private val tableService: TableService
) {
    @PostMapping("/{fullyQualifiedName}")
    fun post(@PathVariable fullyQualifiedName: String, @RequestBody(required = false) note: String?) {
        tableService.setNote(FullyQualifiedName(fullyQualifiedName), Note(note ?: ""))
    }
}
