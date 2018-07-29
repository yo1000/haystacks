package com.yo1000.haystacks.autoconfigure.web

import com.yo1000.haystacks.core.service.TableService
import com.yo1000.haystacks.web.controller.IndexController
import com.yo1000.haystacks.web.controller.InformationController
import com.yo1000.haystacks.web.controller.SearchController
import com.yo1000.haystacks.web.controller.TableController
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Controller
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
class ControllerAutoConfiguration {
    @Controller
    class TableControllerBean(dataSourceProperties: DataSourceProperties)
        : TableController(dataSourceProperties.name)

    @Controller
    class SearchControllerBean(dataSourceProperties: DataSourceProperties)
        : SearchController(dataSourceProperties.name)

    @Controller
    class InformationControllerBean()
        : InformationController()

    @Controller
    class IndexControllerBean()
        : IndexController()
}
