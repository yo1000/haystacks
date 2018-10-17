package com.yo1000.haystacks.autoconfigure.web

import com.yo1000.haystacks.autoconfigure.core.DomainServiceAutoConfiguration
import com.yo1000.haystacks.core.service.TableDomainService
import com.yo1000.haystacks.web.service.TableApplicationService
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.DispatcherServlet
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.servlet.Servlet

/**
 * @author yo1000
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(Servlet::class, DispatcherServlet::class, WebMvcConfigurer::class)
@AutoConfigureAfter(DomainServiceAutoConfiguration::class)
class ApplicationServiceAutoConfiguration {
    @Bean
    fun tableApplicationService(tableDomainService: TableDomainService)
            : TableApplicationService = TableApplicationService(tableDomainService)
}
