package com.yo1000.haystacks.demo

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

/**
 *
 * @author yo1000
 */
@Configuration
@Profile("demo")
class EmbeddedMysql {
    @Bean
    fun mysqlDb(): 
}