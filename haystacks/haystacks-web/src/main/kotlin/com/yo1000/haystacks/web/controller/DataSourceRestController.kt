package com.yo1000.haystacks.web.controller

import com.yo1000.haystacks.web.resource.DataSource
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

/**
 *
 * @author yo1000
 */
@RequestMapping("/api/dataSource")
open class DataSourceRestController(
        private val dataSourceProperties: DataSourceProperties
) {
    @GetMapping("")
    fun get(): DataSource = dataSourceProperties.let {
        DataSource(
                url = dataSourceProperties.url,
                name = dataSourceProperties.name,
                username = dataSourceProperties.username,
                driverClass = dataSourceProperties.driverClassName
        )
    }
}
