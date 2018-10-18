package com.yo1000.haystacks.autoconfigure.web

import com.yo1000.haystacks.web.controller.*
import com.yo1000.haystacks.web.service.TableApplicationService
import com.yo1000.haystacks.web.view.HaystacksDialect
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.DispatcherServlet
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.thymeleaf.dialect.IDialect
import org.thymeleaf.spring5.SpringTemplateEngine
import org.thymeleaf.templateresolver.ITemplateResolver
import javax.servlet.Servlet

@Configuration
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass(Servlet::class, DispatcherServlet::class, WebMvcConfigurer::class)
@AutoConfigureAfter(ApplicationServiceAutoConfiguration::class)
@EnableConfigurationProperties(WebConfigurationProperties::class)
class WebAutoConfiguration {
    @Bean
    fun springTemplateEngine(
            props: ThymeleafProperties,
            templateResolvers: Collection<ITemplateResolver>,
            dialectsProvider: ObjectProvider<Collection<IDialect>>
    ): SpringTemplateEngine = SpringTemplateEngine().also {
        it.enableSpringELCompiler = props.isEnableSpringElCompiler
        templateResolvers.forEach(it::addTemplateResolver)
        dialectsProvider.getIfAvailable { emptyList() }.forEach(it::addDialect)
        it.addDialect(HaystacksDialect())
    }

    @RestController
    @ConditionalOnWebApplication(type = Type.SERVLET)
    @ConditionalOnClass(Servlet::class, DispatcherServlet::class, WebMvcConfigurer::class)
    @AutoConfigureAfter(WebAutoConfiguration::class)
    class TableRestControllerBean(tableApplicationService:TableApplicationService)
        : TableRestController(tableApplicationService)

    @RestController
    @ConditionalOnWebApplication(type = Type.SERVLET)
    @ConditionalOnClass(Servlet::class, DispatcherServlet::class, WebMvcConfigurer::class)
    @AutoConfigureAfter(WebAutoConfiguration::class)
    class SearchRestControllerBean(tableApplicationService:TableApplicationService)
        : SearchRestController(tableApplicationService)

    @RestController
    @ConditionalOnWebApplication(type = Type.SERVLET)
    @ConditionalOnClass(Servlet::class, DispatcherServlet::class, WebMvcConfigurer::class)
    @AutoConfigureAfter(WebAutoConfiguration::class)
    class DataSourceRestControllerBean(dataSourceProperties: DataSourceProperties)
        : DataSourceRestController(dataSourceProperties)

    @RestController
    @ConditionalOnWebApplication(type = Type.SERVLET)
    @ConditionalOnClass(Servlet::class, DispatcherServlet::class, WebMvcConfigurer::class)
    @AutoConfigureAfter(WebAutoConfiguration::class)
    class NoteRestControllerBean(tableApplicationService:TableApplicationService)
        : NoteRestController(tableApplicationService)

    @Controller
    @ConditionalOnWebApplication(type = Type.SERVLET)
    @ConditionalOnClass(Servlet::class, DispatcherServlet::class, WebMvcConfigurer::class)
    @AutoConfigureAfter(WebAutoConfiguration::class)
    class TableControllerBean(
            props: WebConfigurationProperties,
            dataSourceProperties: DataSourceProperties,
            tableApplicationService: TableApplicationService
    ) : TableController(
            props.ssr,
            dataSourceProperties.name,
            tableApplicationService
    )

    @Controller
    @ConditionalOnWebApplication(type = Type.SERVLET)
    @ConditionalOnClass(Servlet::class, DispatcherServlet::class, WebMvcConfigurer::class)
    @AutoConfigureAfter(WebAutoConfiguration::class)
    class SearchControllerBean(
            props: WebConfigurationProperties,
            dataSourceProperties: DataSourceProperties,
            tableApplicationService: TableApplicationService
    ) : SearchController(
            props.ssr,
            dataSourceProperties.name,
            tableApplicationService
    )

    @Controller
    @ConditionalOnWebApplication(type = Type.SERVLET)
    @ConditionalOnClass(Servlet::class, DispatcherServlet::class, WebMvcConfigurer::class)
    @AutoConfigureAfter(WebAutoConfiguration::class)
    class InformationControllerBean(
            props: WebConfigurationProperties,
            dataSourceProperties: DataSourceProperties
    ) : InformationController(
            props.ssr,
            dataSourceProperties
    )

    @Controller
    @ConditionalOnWebApplication(type = Type.SERVLET)
    @ConditionalOnClass(Servlet::class, DispatcherServlet::class, WebMvcConfigurer::class)
    @AutoConfigureAfter(WebAutoConfiguration::class)
    class IndexControllerBean : IndexController()
}

@ConfigurationProperties("haystacks.web")
class WebConfigurationProperties(
        var ssr: Boolean = true
)
