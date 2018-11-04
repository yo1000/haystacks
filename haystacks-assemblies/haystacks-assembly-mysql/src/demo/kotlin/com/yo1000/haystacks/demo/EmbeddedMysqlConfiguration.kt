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
        dataSource.connection.createStatement().use {
            listOf("""
                CREATE TABLE `countries` (
                    `id`    varchar(4)  NOT NULL    COMMENT 'ID',
                    `name`  varchar(40) NOT NULL    COMMENT 'Country name',
                    PRIMARY KEY(`id`),
                    UNIQUE KEY `uq_countries_name` (`name`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Countries';
                """.trimIndent(), """
                CREATE TABLE `artists` (
                    `id`            varchar(4)  NOT NULL    COMMENT 'ID',
                    `name`          varchar(40) NOT NULL    COMMENT 'Artist name',
                    `country_id`    varchar(4)  NOT NULL    COMMENT 'Country ID',
                    PRIMARY KEY(`id`),
                    CONSTRAINT `fk_artists_country_id`  FOREIGN KEY (`country_id`)  REFERENCES `countries`  (`id`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Artists';
                """.trimIndent(), """
                CREATE TABLE `mediums` (
                    `id`          varchar(4)  NOT NULL    COMMENT 'ID',
                    `description` varchar(80) NOT NULL    COMMENT 'Description',
                    PRIMARY KEY(`id`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
                """.trimIndent(), """
                CREATE TABLE `pictures` (
                    `id`          varchar(4)  NOT NULL    COMMENT 'ID',
                    `title`       varchar(80) NOT NULL    COMMENT 'Title',
                    `country_id`  varchar(4)  NOT NULL    COMMENT 'Country ID',
                    `artist_id`   varchar(4)  NOT NULL    COMMENT 'Artist ID',
                    `medium_id`   varchar(4)              COMMENT 'Medium ID',
                    PRIMARY KEY(`id`),
                    CONSTRAINT `fk_pictures_country_id`   FOREIGN KEY (`country_id`)  REFERENCES `countries`  (`id`),
                    CONSTRAINT `fk_pictures_artist_id`    FOREIGN KEY (`artist_id`)   REFERENCES `artists`    (`id`),
                    CONSTRAINT `fk_pictures_medium_id`    FOREIGN KEY (`medium_id`)   REFERENCES `mediums`    (`id`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Pictures';
                """.trimIndent()
            ).forEach(it::addBatch)
            it.executeBatch()

            listOf("""
                INSERT INTO `countries` VALUES ('1000', 'France');
                """.trimIndent(), """
                INSERT INTO `artists` VALUES ('2000', 'Claude Monet', '1000');
                """.trimIndent(), """
                INSERT INTO `artists` VALUES ('2001', 'Gustave Caillebotte', '1000');
                """.trimIndent(), """
                INSERT INTO `mediums` VALUES ('3000', 'Oil on canvas');
                """.trimIndent(), """
                INSERT INTO `pictures` VALUES ('4000', 'Haystacks', '1000', '2000', '3000');
                """.trimIndent(), """
                INSERT INTO `pictures` VALUES ('4001', 'Water Lilies', '1000', '2000', '3000');
                """.trimIndent()
            ).forEach(it::addBatch)
            it.executeBatch()
        }
    }
}