package com.yo1000.haystacks.autoconfigure.data.mysql

import com.mysql.jdbc.Driver
import com.yo1000.haystacks.autoconfigure.core.DomainServiceAutoConfiguration
import com.yo1000.haystacks.core.repository.IndexRepository
import com.yo1000.haystacks.core.repository.TableRepository
import com.yo1000.haystacks.data.repository.mysql.MysqlIndexRepository
import com.yo1000.haystacks.data.repository.mysql.MysqlTableRepository
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import javax.sql.DataSource

/**
 * @author yo1000
 */
@Configuration
@ConditionalOnClass(DataSource::class, JdbcTemplate::class, Driver::class)
@ConditionalOnSingleCandidate(DataSource::class)
@AutoConfigureAfter(JdbcTemplateAutoConfiguration::class)
@AutoConfigureBefore(DomainServiceAutoConfiguration::class)
class MysqlRepositoryAutoConfiguration {
    @Bean
    fun tableRepository(dataSourceProperties: DataSourceProperties, jdbcTemplate: NamedParameterJdbcTemplate)
            : TableRepository = MysqlTableRepository(jdbcTemplate, dataSourceProperties.name)

    @Bean
    fun indexRepository(dataSourceProperties: DataSourceProperties, jdbcTemplate: NamedParameterJdbcTemplate)
            : IndexRepository = MysqlIndexRepository(jdbcTemplate, dataSourceProperties.name)
}
