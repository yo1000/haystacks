package com.yo1000.haystacks.autoconfigure.web

import com.yo1000.haystacks.autoconfigure.core.NoteFileConfigurationProperties
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
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.DispatcherServlet
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.thymeleaf.dialect.IDialect
import org.thymeleaf.spring5.SpringTemplateEngine
import org.thymeleaf.templateresolver.ITemplateResolver
import java.nio.file.Paths
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

    @ControllerAdvice
    @ConditionalOnWebApplication(type = Type.SERVLET)
    @ConditionalOnClass(Servlet::class, DispatcherServlet::class, WebMvcConfigurer::class)
    @AutoConfigureAfter(WebAutoConfiguration::class)
    class ModelConfigureControllerAdvice(
            private val webProps: WebConfigurationProperties,
            private val dataSourceProps: DataSourceProperties
    ) {
        @ModelAttribute
        fun addObjects(model: Model) {
            model.addAttribute("logo", webProps.logo)
            model.addAttribute("title", webProps.title)
            model.addAttribute("favicon", webProps.favicon)
            model.addAttribute("dataSourceName", dataSourceProps.name)
        }
    }

    @RestController
    @ConditionalOnWebApplication(type = Type.SERVLET)
    @ConditionalOnClass(Servlet::class, DispatcherServlet::class, WebMvcConfigurer::class)
    @AutoConfigureAfter(WebAutoConfiguration::class)
    class TableRestControllerBean(
            tableApplicationService: TableApplicationService
    ) : TableRestController(
            tableApplicationService
    )

    @RestController
    @ConditionalOnWebApplication(type = Type.SERVLET)
    @ConditionalOnClass(Servlet::class, DispatcherServlet::class, WebMvcConfigurer::class)
    @AutoConfigureAfter(WebAutoConfiguration::class)
    class SearchRestControllerBean(
            tableApplicationService: TableApplicationService
    ) : SearchRestController(
            tableApplicationService
    )

    @RestController
    @ConditionalOnWebApplication(type = Type.SERVLET)
    @ConditionalOnClass(Servlet::class, DispatcherServlet::class, WebMvcConfigurer::class)
    @AutoConfigureAfter(WebAutoConfiguration::class)
    class InformationRestControllerBean(
            dataSourceProperties: DataSourceProperties,
            noteFileConfigurationProperties: NoteFileConfigurationProperties
    ) : InformationRestController(
            dataSourceUrl = dataSourceProperties.url,
            dataSourceName = dataSourceProperties.name,
            dataSourceUsername = dataSourceProperties.username,
            dataSourceDriverClassName = dataSourceProperties.driverClassName,
            noteFilePath = noteFileConfigurationProperties.storeLocation.takeIf {
                !it.trim().isEmpty()
            }?.let {
                Paths.get(it)
            } ?: Paths.get(System.getProperty("user.dir")).resolve("note.properties")
    )

    @RestController
    @ConditionalOnWebApplication(type = Type.SERVLET)
    @ConditionalOnClass(Servlet::class, DispatcherServlet::class, WebMvcConfigurer::class)
    @AutoConfigureAfter(WebAutoConfiguration::class)
    class NoteRestControllerBean(
            tableApplicationService: TableApplicationService
    ) : NoteRestController(
            tableApplicationService
    )

    @Controller
    @ConditionalOnWebApplication(type = Type.SERVLET)
    @ConditionalOnClass(Servlet::class, DispatcherServlet::class, WebMvcConfigurer::class)
    @AutoConfigureAfter(WebAutoConfiguration::class)
    class TableControllerBean(
            props: WebConfigurationProperties,
            tableApplicationService: TableApplicationService
    ) : TableController(
            props.ssr,
            tableApplicationService
    )

    @Controller
    @ConditionalOnWebApplication(type = Type.SERVLET)
    @ConditionalOnClass(Servlet::class, DispatcherServlet::class, WebMvcConfigurer::class)
    @AutoConfigureAfter(WebAutoConfiguration::class)
    class SearchControllerBean(
            props: WebConfigurationProperties,
            tableApplicationService: TableApplicationService
    ) : SearchController(
            props.ssr,
            tableApplicationService
    )

    @Controller
    @ConditionalOnWebApplication(type = Type.SERVLET)
    @ConditionalOnClass(Servlet::class, DispatcherServlet::class, WebMvcConfigurer::class)
    @AutoConfigureAfter(WebAutoConfiguration::class)
    class InformationControllerBean(
            props: WebConfigurationProperties,
            dataSourceProperties: DataSourceProperties,
            noteFileProperties: NoteFileConfigurationProperties
    ) : InformationController(
            props.ssr,
            dataSourceProperties,
            noteFileProperties.storeLocation.takeIf {
                !it.trim().isEmpty()
            }?.let {
                Paths.get(it)
            } ?: Paths.get(System.getProperty("user.dir")).resolve("note.properties")
    )

    @Controller
    @ConditionalOnWebApplication(type = Type.SERVLET)
    @ConditionalOnClass(Servlet::class, DispatcherServlet::class, WebMvcConfigurer::class)
    @AutoConfigureAfter(WebAutoConfiguration::class)
    class NoteControllerBean(
            props: NoteFileConfigurationProperties
    ) : NoteController(
            props.storeLocation.takeIf {
                !it.trim().isEmpty()
            }?.let {
                Paths.get(it)
            } ?: Paths.get(System.getProperty("user.dir")).resolve("note.properties")
    )

    @Controller
    @ConditionalOnWebApplication(type = Type.SERVLET)
    @ConditionalOnClass(Servlet::class, DispatcherServlet::class, WebMvcConfigurer::class)
    @AutoConfigureAfter(WebAutoConfiguration::class)
    class IndexControllerBean : IndexController()
}

@ConfigurationProperties("haystacks.web")
class WebConfigurationProperties(
        var ssr: Boolean = true,
        var logo: String = "/img/haystacks-logo.svg",
        var title: String = "haystacks",
        var favicon: String = "/favicon.ico"
)
