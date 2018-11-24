package com.yo1000.haystacks.autoconfigure.core

import com.yo1000.haystacks.core.service.TableDomainService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@Configuration
@EnableCaching
@EnableScheduling
@ConditionalOnProperty(
        prefix = CacheProperties.PREFIX,
        name = ["enabled"],
        havingValue = "true",
        matchIfMissing = true
)
class CacheAutoConfiguration(
        private val tableDomainService: TableDomainService
) {
    // initial: 0, each: 2h
    @Scheduled(initialDelay =  0L, fixedRate = 1000L * 60 * 60 * 2)
    fun cacheTableOutlines() {
        tableDomainService.getTableOutlines()
    }

    // initial: 1h 59m, each: 2h
    @Scheduled(initialDelay =  1000L * 60 * 60 * 2 - 1000L * 60, fixedRate = 1000L * 60 * 60 * 2)
    @CacheEvict(TableDomainService.CACHE_NAME_TABLE_OUTLINES)
    fun evictTableOutlines() {}
}

@ConfigurationProperties(CacheProperties.PREFIX)
class CacheProperties(
        var enabled: Boolean = true
) {
    companion object {
        const val PREFIX = "haystacks.cache"
    }
}
