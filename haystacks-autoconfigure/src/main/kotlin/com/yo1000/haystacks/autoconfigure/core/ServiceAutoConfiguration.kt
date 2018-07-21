package com.yo1000.haystacks.autoconfigure.core

import com.yo1000.haystacks.core.repository.IndexRepository
import com.yo1000.haystacks.core.repository.TableRepository
import com.yo1000.haystacks.core.service.TableService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author yo1000
 */
@Configuration
@ConditionalOnBean(TableRepository::class, IndexRepository::class)
class ServiceAutoConfiguration {
    @Bean
    fun tableService(tableRepository: TableRepository, indexRepository: IndexRepository)
            : TableService = TableService(tableRepository, indexRepository)
}
