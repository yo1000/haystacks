package com.yo1000.haystacks.data.repository.mysql

import com.mysql.jdbc.Driver
import com.wix.mysql.EmbeddedMysql
import com.wix.mysql.config.Charset
import com.wix.mysql.config.MysqldConfig
import com.wix.mysql.distribution.Version
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

class MysqlTableRepositoryTests {
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

    lateinit var namedParameterJdbcOperations: NamedParameterJdbcOperations

    @BeforeEach
    fun setupDataSource() {
        namedParameterJdbcOperations = NamedParameterJdbcTemplate(dataSource)

        dataSource.connection.use {
            it.createStatement().use {
                listOf(
                        "DELETE FROM `pictures`",
                        "DELETE FROM `artists`",
                        "DELETE FROM `mediums`",
                        "DELETE FROM `countries`"
                ).forEach(it::addBatch)
                it.executeBatch()

                listOf(
                        "INSERT INTO `countries` VALUES ('1000', 'France')",
                        "INSERT INTO `mediums`   VALUES ('3000', 'Oil on canvas')",
                        "INSERT INTO `artists`   VALUES ('2000', 'Claude Monet'       , '1000')",
                        "INSERT INTO `artists`   VALUES ('2001', 'Gustave Caillebotte', '1000')",
                        "INSERT INTO `pictures`  VALUES ('4000', 'Haystacks'   , '1000', '2000', '3000')",
                        "INSERT INTO `pictures`  VALUES ('4001', 'Water Lilies', '1000', '2000', '3000')"
                ).forEach(it::addBatch)
                it.executeBatch()
            }
        }
    }

    @Test
    fun `Given a MysqlTableRepository when invoke findTableNamesAll then should return TableNames list of table schemas`() {
        // Given
        val repos = MysqlTableRepository(namedParameterJdbcOperations, DATABASE_NAME)

        // When
        val actual = repos.findTableNamesAll()

        // Then
        Assertions.assertEquals(4, actual.size)
        listOf(
                TableNames(logicalName = LogicalName("Countries"), physicalName = TablePhysicalName("countries")),
                TableNames(logicalName = LogicalName("Artists"), physicalName = TablePhysicalName("artists")),
                TableNames(logicalName = LogicalName(""), physicalName = TablePhysicalName("mediums")),
                TableNames(logicalName = LogicalName("Pictures"), physicalName = TablePhysicalName("pictures"))
        ).forEach {
            Assertions.assertTrue {
                actual.contains(it)
            }
        }
    }

    @ParameterizedTest
    @CsvSource(value = [
        "'countries', 'Countries', 2, 1",
        "'artists'  , 'Artists'  , 3, 2",
        "'mediums'  , ''         , 2, 1",
        "'pictures' , 'Pictures' , 5, 2"
    ])
    fun `Given a MysqlTableRepository when invoke findTable by name then should return Table object`(
            physicalName: String, logicalName: String, columnCount: Int, rowCount: Long
    ) {
        // Given
        val repos = MysqlTableRepository(namedParameterJdbcOperations, DATABASE_NAME)

        // When
        val actual = repos.findTable(TablePhysicalName(physicalName))

        // Then
        Assertions.assertEquals(physicalName, actual.names.physicalName.value)
        Assertions.assertEquals(logicalName, actual.names.logicalName.value)
        Assertions.assertEquals(columnCount, actual.columns.size)
        Assertions.assertEquals(rowCount, actual.rowCount)
    }

    @ParameterizedTest
    @CsvSource(value = [
        "'countries', 2",
        "'artists'  , 3",
        "'mediums'  , 2",
        "'pictures' , 5"
    ])
    fun `Given a MysqlTableRepository when invoke findColumnCountMap then should return column count map`(
            physicalName: String, columnCount: Int
    ) {
        // Given
        val repos = MysqlTableRepository(namedParameterJdbcOperations, DATABASE_NAME)

        // When
        val actual = repos.findColumnCountMap()

        // Then
        Assertions.assertEquals(4, actual.size)
        Assertions.assertEquals(columnCount, actual[TablePhysicalName(physicalName)])
    }

    @ParameterizedTest
    @CsvSource(value = [
        "'countries', 1",
        "'artists'  , 2",
        "'mediums'  , 1",
        "'pictures' , 2"
    ])
    fun `Given a MysqlTableRepository when invoke findRowCountMap then should return row count map`(
            physicalName: String, rowCount: Long
    ) {
        // Given
        val repos = MysqlTableRepository(namedParameterJdbcOperations, DATABASE_NAME)

        // When
        val actual = repos.findRowCountMap()

        // Then
        Assertions.assertEquals(4, actual.size)
        Assertions.assertEquals(rowCount, actual[TablePhysicalName(physicalName)])
    }

    @ParameterizedTest
    @CsvSource(value = [
        "'countries', 2",
        "'artists'  , 1",
        "'mediums'  , 1"
    ])
    fun `Given a MysqlTableRepository when invoke findReferencedCountFromChildrenMap then should return referenced count from children map`(
            physicalName: String, referencedCount: Int
    ) {
        // Given
        val repos = MysqlTableRepository(namedParameterJdbcOperations, DATABASE_NAME)

        // When
        val actual = repos.findReferencedCountFromChildrenMap()

        // Then
        Assertions.assertEquals(3, actual.size)
        Assertions.assertEquals(referencedCount, actual[TablePhysicalName(physicalName)])
    }

    @ParameterizedTest
    @CsvSource(value = [
        "'artists'  , 1",
        "'pictures' , 3"
    ])
    fun `Given a MysqlTableRepository when invoke findReferencingCountToParentMap then should return referencing count to parent map`(
            physicalName: String, referencingCount: Int
    ) {
        // Given
        val repos = MysqlTableRepository(namedParameterJdbcOperations, DATABASE_NAME)

        // When
        val actual = repos.findReferencingCountToParentMap()

        // Then
        Assertions.assertEquals(2, actual.size)
        Assertions.assertEquals(referencingCount, actual[TablePhysicalName(physicalName)])
    }

    @ParameterizedTest
    @CsvSource(value = [
        "'countries', 'CREATE TABLE `countries`'",
        "'artists'  , 'CREATE TABLE `artists`'",
        "'mediums'  , 'CREATE TABLE `mediums`'",
        "'pictures' , 'CREATE TABLE `pictures`'"
    ])
    fun `Given a MysqlTableRepository when invoke findStatementByName then should return create sql statement`(
            physicalName: String, statement: String
    ) {
        // Given
        val repos = MysqlTableRepository(namedParameterJdbcOperations, DATABASE_NAME)

        // When
        val actual = repos.findStatementByName(TablePhysicalName(physicalName))

        // Then
        Assertions.assertTrue {
            actual.value.startsWith(statement)
        }
    }

    @ParameterizedTest
    @CsvSource(value = [
        "'countries', 1",
        "'art'      , 2",
        "'name'     , 2",
        "'_id'      , 2"
    ])
    fun `Given a MysqlTableRepository when invoke findNames by search query then should return found names`(
            q: String, size: Int
    ) {
        // Given
        val repos = MysqlTableRepository(namedParameterJdbcOperations, DATABASE_NAME)

        // When
        val actual = repos.findNames(q)

        // Then
        Assertions.assertEquals(size, actual.size)
        Assertions.assertTrue(actual.all {
            it.columnNamesList.any { it.logicalName.value.contains(q) || it.physicalName.value.contains(q) } ||
                    it.tableNames.physicalName.value.contains(q) ||
                    it.tableNames.logicalName.value.contains(q)
        })
    }
}
