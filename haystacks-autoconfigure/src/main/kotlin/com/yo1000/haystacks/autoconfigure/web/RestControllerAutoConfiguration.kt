package com.yo1000.haystacks.autoconfigure.web

import com.yo1000.haystacks.core.service.TableService
import com.yo1000.haystacks.web.controller.SearchRestController
import com.yo1000.haystacks.web.controller.TableRestController
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.DispatcherServlet
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.servlet.Servlet

/**
 * @author yo1000
 */
@Configuration
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass(Servlet::class, DispatcherServlet::class, WebMvcConfigurer::class)
@ConditionalOnBean(TableService::class)
@ConditionalOnMissingBean(WebMvcConfigurationSupport::class)
@AutoConfigureAfter(DispatcherServletAutoConfiguration::class, ValidationAutoConfiguration::class)
class RestControllerAutoConfiguration {
    @RestController
    class TableRestControllerBean(tableService: TableService)
        : TableRestController(tableService)

    @RestController
    class SearchRestControllerBean(tableService: TableService)
        : SearchRestController(tableService)
}
