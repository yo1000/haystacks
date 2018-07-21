package com.yo1000.haystacks.autoconfigure.data.mysql

import com.yo1000.haystacks.core.repository.IndexRepository
import com.yo1000.haystacks.core.repository.TableRepository
import com.yo1000.haystacks.data.repository.mysql.MysqlIndexRepository
import com.yo1000.haystacks.data.repository.mysql.MysqlTableRepository
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.jdbc.JdbcProperties
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import javax.sql.DataSource

/**
 * @author yo1000
 */
@Configuration
@ConditionalOnClass(DataSource::class, JdbcTemplate::class)
@ConditionalOnSingleCandidate(DataSource::class)
@AutoConfigureAfter(JdbcTemplateAutoConfiguration::class)
class MysqlRepositoryAutoConfiguration {
    @Bean
    fun tableRepository(dataSourceProperties: DataSourceProperties, jdbcTemplate: NamedParameterJdbcTemplate)
            : TableRepository = MysqlTableRepository(jdbcTemplate, dataSourceProperties.name)

    @Bean
    fun indexRepository(dataSourceProperties: DataSourceProperties, jdbcTemplate: NamedParameterJdbcTemplate)
            : IndexRepository = MysqlIndexRepository(jdbcTemplate, dataSourceProperties.name)
}
