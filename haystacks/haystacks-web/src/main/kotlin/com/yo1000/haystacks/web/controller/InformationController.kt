package com.yo1000.haystacks.web.controller

import com.yo1000.haystacks.web.resource.DataSource
import com.yo1000.haystacks.web.service.TableApplicationService
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

/**
 * @author yo1000
 */
@RequestMapping("/info")
open class InformationController(
        private val ssr: Boolean,
        private val dataSourceProperties: DataSourceProperties
) {
    @GetMapping("")
    fun get(model: Model): String = if (ssr) {
        model.addAttribute("dataSource", DataSource(
                url = dataSourceProperties.url,
                name = dataSourceProperties.name,
                username = dataSourceProperties.username,
                driverClass = dataSourceProperties.driverClassName
        ))
        "info.ssr"
    } else {
        "info"
    }
}
