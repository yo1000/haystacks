package com.yo1000.haystacks.data.repository.mysql

import com.mysql.jdbc.Driver
import com.wix.mysql.EmbeddedMysql
import com.wix.mysql.config.Charset
import com.wix.mysql.config.MysqldConfig
import com.wix.mysql.distribution.Version
import com.yo1000.haystacks.core.entity.Index
import com.yo1000.haystacks.core.entity.TableNames
import com.yo1000.haystacks.core.valueobject.LogicalName
import com.yo1000.haystacks.core.valueobject.TablePhysicalName
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

class MysqlIndexRepositoryTests {
    companion object {
        const val DATABASE_USERNAME = "sa"
        const val DATABASE_NAME = "testdb"

        lateinit var embeddedMysql: EmbeddedMysql
        lateinit var dataSource: DataSource

        @JvmStatic
        @BeforeAll
        fun startEmbeddedMysql() {
            embeddedMysql = EmbeddedMysql.anEmbeddedMysql(
                    MysqldConfig.aMysqldConfig(Version.v5_7_latest)
                            .withCharset(Charset.UTF8)
                            .withFreePort()
                            .withUser(DATABASE_USERNAME, "")
                            .build()
            ).addSchema(DATABASE_NAME).start()

            val driverManagerDataSource = DriverManagerDataSource()
            driverManagerDataSource.setDriverClassName(Driver::class.java.name)
            driverManagerDataSource.url = "jdbc:mysql://localhost:${embeddedMysql.config.port}/$DATABASE_NAME"
            driverManagerDataSource.username = DATABASE_USERNAME

            dataSource = driverManagerDataSource

            dataSource.connection.use {
                it.createStatement().use {
                    listOf("""
                        CREATE TABLE `countries` (
                            `id`            varchar(4)  NOT NULL    COMMENT 'ID',
                            `name`          varchar(40) NOT NULL    COMMENT 'Country name',
                            PRIMARY KEY(`id`),
                            UNIQUE KEY `uq_countries_name` (`name`)
                        ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Countries';
                    """.trimIndent(), """
                        CREATE TABLE `artists` (
                            `id`            varchar(4)  NOT NULL    COMMENT 'ID',
                            `name`          varchar(40) NOT NULL    COMMENT 'Artist name',
                            `country_id`    varchar(4)  NOT NULL    COMMENT 'Country ID',
                            PRIMARY KEY(`id`),
                            CONSTRAINT      `fk_artists_country_id`     FOREIGN KEY (`country_id`)  REFERENCES `countries`  (`id`)
                        ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Artists';
                    """.trimIndent(), """
                        CREATE TABLE `mediums` (
                            `id`            varchar(4)  NOT NULL    COMMENT 'ID',
                            `description`   varchar(80) NOT NULL    COMMENT 'Description',
                            PRIMARY KEY(`id`)
                        ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
                    """.trimIndent(), """
                        CREATE TABLE `pictures` (
                            `id`            varchar(4)  NOT NULL    COMMENT 'ID',
                            `title`         varchar(80) NOT NULL    COMMENT 'Title',
                            `country_id`    varchar(4)  NOT NULL    COMMENT 'Country ID',
                            `artist_id`     varchar(4)  NOT NULL    COMMENT 'Artist ID',
                            `medium_id`     varchar(4)              COMMENT 'Medium ID',
                            PRIMARY KEY(`id`),
                            CONSTRAINT      `fk_pictures_country_id`    FOREIGN KEY (`country_id`)  REFERENCES `countries`  (`id`),
                            CONSTRAINT      `fk_pictures_artist_id`     FOREIGN KEY (`artist_id`)   REFERENCES `artists`    (`id`),
                            CONSTRAINT      `fk_pictures_medium_id`     FOREIGN KEY (`medium_id`)   REFERENCES `mediums`    (`id`)
                        ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Pictures';
                    """.trimIndent()
                    ).forEach(it::addBatch)
                    it.executeBatch()
                }
            }
        }

        @JvmStatic
        @AfterAll
        fun stopEmbeddedMysql() {
            embeddedMysql.stop()
        }
    }

    @Test
    fun `Given a MysqlIndexRepository when invoke findByTableName then should return Index object suitable for table`() {
        // Given
        val namedParameterJdbcOperations = NamedParameterJdbcTemplate(dataSource)
        val repos = MysqlIndexRepository(namedParameterJdbcOperations, DATABASE_NAME)

        // When
        var actual = repos.findByTableName(TablePhysicalName("countries"))

        // Then
        Assertions.assertEquals(2, actual.size)

        Assertions.assertEquals(Index.Type.PRIMARY, actual[0].type)
        Assertions.assertEquals("PRIMARY", actual[0].names.physicalName.value)
        Assertions.assertEquals("id", actual[0].columnNames[0].value)

        Assertions.assertEquals(Index.Type.UNIQUE, actual[1].type)
        Assertions.assertEquals("uq_countries_name", actual[1].names.physicalName.value)
        Assertions.assertEquals("name", actual[1].columnNames[0].value)

        // When
        actual = repos.findByTableName(TablePhysicalName("artists"))

        // Then
        Assertions.assertEquals(2, actual.size)

        Assertions.assertEquals(Index.Type.PRIMARY, actual[0].type)
        Assertions.assertEquals("PRIMARY", actual[0].names.physicalName.value)
        Assertions.assertEquals("id", actual[0].columnNames[0].value)

        Assertions.assertEquals(Index.Type.PERFORMANCE, actual[1].type)
        Assertions.assertEquals("fk_artists_country_id", actual[1].names.physicalName.value)
        Assertions.assertEquals("country_id", actual[1].columnNames[0].value)

        // When
        actual = repos.findByTableName(TablePhysicalName("mediums"))

        // Then
        Assertions.assertEquals(1, actual.size)

        Assertions.assertEquals(Index.Type.PRIMARY, actual[0].type)
        Assertions.assertEquals("PRIMARY", actual[0].names.physicalName.value)
        Assertions.assertEquals("id", actual[0].columnNames[0].value)

        // When
        actual = repos.findByTableName(TablePhysicalName("pictures"))

        // Then
        Assertions.assertEquals(4, actual.size)

        Assertions.assertEquals(Index.Type.PRIMARY, actual[0].type)
        Assertions.assertEquals("PRIMARY", actual[0].names.physicalName.value)
        Assertions.assertEquals("id", actual[0].columnNames[0].value)

        Assertions.assertTrue {
            actual.subList(1, 4).any {
                it.type == Index.Type.PERFORMANCE
                        && it.names.physicalName.value == "fk_pictures_artist_id"
                        && it.columnNames[0].value == "artist_id"
            }
        }

        Assertions.assertTrue {
            actual.subList(1, 4).any {
                it.type == Index.Type.PERFORMANCE
                        && it.names.physicalName.value == "fk_pictures_country_id"
                        && it.columnNames[0].value == "country_id"
            }
        }

        Assertions.assertTrue {
            actual.subList(1, 4).any {
                it.type == Index.Type.PERFORMANCE
                        && it.names.physicalName.value == "fk_pictures_medium_id"
                        && it.columnNames[0].value == "medium_id"
            }
        }
    }
}
