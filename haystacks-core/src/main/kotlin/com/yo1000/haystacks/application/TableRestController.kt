package com.yo1000.haystacks.application

import com.yo1000.haystacks.domain.entity.TableOutline
import com.yo1000.haystacks.domain.service.TableService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 *
 * @author yo1000
 */
@RestController
@RequestMapping("/tables")
class TableRestController(
        private val tableService: TableService
) {
    @GetMapping("")
    fun get(): List<TableOutline> = tableService.getTableOutlines()
}
