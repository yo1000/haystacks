package com.yo1000.haystacks.web.controller

import com.yo1000.haystacks.core.service.TableService
import com.yo1000.haystacks.web.resource.SearchResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 *
 * @author yo1000
 */
@RequestMapping("/api/search")
open class SearchRestController(
        private val tableService: TableService
) {
    @GetMapping("")
    fun get(@RequestParam q: List<String>): List<SearchResult> = tableService.find(*q.toTypedArray()).map {
        SearchResult(
                table = SearchResult.Table(
                        name = it.tableNames.physicalName.value,
                        comment = it.tableNames.logicalName.value
                ),
                columns = it.columnNamesList.map {
                    SearchResult.Column(
                            name = it.physicalName.value,
                            comment = it.logicalName.value
                    )
                }
        )
    }
}
