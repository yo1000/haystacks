package com.yo1000.haystacks.demo

import com.wix.mysql.EmbeddedMysql
import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.config.Charset
import com.wix.mysql.config.MysqldConfig.aMysqldConfig
import com.wix.mysql.distribution.Version
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import javax.sql.DataSource

/**
 *
 * @author yo1000
 */
@Configuration
@Profile("demo")
class EmbeddedMysqlConfiguration {
    @Bean
    fun embeddedMysql(): EmbeddedMysql = anEmbeddedMysql(
            aMysqldConfig(Version.v5_7_latest)
                    .withCharset(Charset.UTF8)
                    .withPort(3306)
                    .withUser("sa", "")
                    .build()
    ).addSchema("demo").start()
}

@Component
@Profile("demo")
class EmbeddedMysqlInitializer(
        private val dataSource: DataSource,
        private val embeddedMysql: EmbeddedMysql
) {
    @PostConstruct
    fun setup() {
        val statement = dataSource.connection.createStatement()
        val sql = listOf("""
            CREATE TABLE `authors` (
                `id`    varchar(4)  NOT NULL    COMMENT 'ID',
                `name`  varchar(40) NOT NULL    COMMENT 'Author name',
                PRIMARY KEY(`id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Authors';
            """.trimIndent(), """
            INSERT INTO authors VALUES ('1000', 'ISHIKAWA Takuboku');
            """.trimIndent(), """
            INSERT INTO authors VALUES ('1001', 'DAZAI Osamu');
            """.trimIndent(), """
            CREATE TABLE `books` (
              `id`          varchar(4)  NOT NULL    COMMENT 'ID',
              `title`       varchar(80) NOT NULL    COMMENT 'Title',
              `author_id`   varchar(4)  NOT NULL    COMMENT 'Author ID',
              PRIMARY KEY(`id`),
              CONSTRAINT `fk_books_author_id`   FOREIGN KEY (`author_id`) REFERENCES `authors` (`id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Books';
            """.trimIndent()
        )

        sql.forEach(statement::addBatch)
        statement.executeBatch()
    }
}