package com.yo1000.haystacks.demo

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres
import ru.yandex.qatools.embed.postgresql.distribution.Version
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.sql.DataSource

/**
 *
 * @author yo1000
 */
@Configuration
@Profile("demo")
class EmbeddedPostgresqlConfiguration {
    private lateinit var embeddedPostgresBean: EmbeddedPostgres

    @PostConstruct
    fun startEmbeddedPostgres() {
        embeddedPostgresBean = EmbeddedPostgres(Version.V9_6_8)
        embeddedPostgresBean.start("localhost", 5432, "demo", "pg", "pg")
    }

    @PreDestroy
    fun stopEmbeddedPostgres() {
        embeddedPostgresBean.stop()
    }
}

@Component
@Profile("demo")
class EmbeddedPostgresqlInitializer(
        private val dataSource: DataSource
) {
    @PostConstruct
    fun setup() {
        dataSource.connection.createStatement().use {
            listOf("""
                CREATE TABLE countries (
                    id    varchar(4)  NOT NULL,
                    name  varchar(40) NOT NULL UNIQUE,
                    PRIMARY KEY(id)
                );
                """.trimIndent(), """
                COMMENT ON TABLE countries IS 'Countries';
                """.trimIndent(), """
                COMMENT ON COLUMN countries.id IS 'ID';
                """.trimIndent(), """
                COMMENT ON COLUMN countries.name IS 'Country name';
                """.trimIndent(), """
                CREATE TABLE artists (
                    id            varchar(4)  NOT NULL,
                    name          varchar(40) NOT NULL,
                    country_id    varchar(4)  NOT NULL,
                    PRIMARY KEY(id),
                    CONSTRAINT fk_artists_country_id  FOREIGN KEY (country_id)  REFERENCES countries  (id)
                );
                """.trimIndent(), """
                COMMENT ON TABLE artists IS 'Artists';
                """.trimIndent(), """
                COMMENT ON COLUMN artists.id IS 'ID';
                """.trimIndent(), """
                COMMENT ON COLUMN artists.name IS 'Artist name';
                """.trimIndent(), """
                COMMENT ON COLUMN artists.country_id IS 'Country ID';
                """.trimIndent(), """
                CREATE TABLE mediums (
                    id          varchar(4)  NOT NULL,
                    description varchar(80) NOT NULL,
                    PRIMARY KEY(id)
                );
                """.trimIndent(), """
                COMMENT ON COLUMN mediums.id IS 'ID';
                """.trimIndent(), """
                COMMENT ON COLUMN mediums.description IS 'Description';
                 """.trimIndent(), """
                CREATE TABLE pictures (
                    id          varchar(4)  NOT NULL,
                    title       varchar(80) NOT NULL,
                    country_id  varchar(4)  NOT NULL,
                    artist_id   varchar(4)  NOT NULL,
                    medium_id   varchar(4),
                    PRIMARY KEY(id),
                    CONSTRAINT fk_pictures_country_id   FOREIGN KEY (country_id)  REFERENCES countries  (id),
                    CONSTRAINT fk_pictures_artist_id    FOREIGN KEY (artist_id)   REFERENCES artists    (id),
                    CONSTRAINT fk_pictures_medium_id    FOREIGN KEY (medium_id)   REFERENCES mediums    (id)
                );
                """.trimIndent(), """
                COMMENT ON TABLE pictures IS 'Pictures';
                """.trimIndent(), """
                COMMENT ON COLUMN pictures.id IS 'ID';
                """.trimIndent(), """
                COMMENT ON COLUMN pictures.title IS 'Title';
                """.trimIndent(), """
                COMMENT ON COLUMN pictures.country_id IS 'Country ID';
                """.trimIndent(), """
                COMMENT ON COLUMN pictures.artist_id IS 'Artist ID';
                """.trimIndent(), """
                COMMENT ON COLUMN pictures.medium_id IS 'Medium ID';
                """.trimIndent()
            ).forEach(it::addBatch)
            it.executeBatch()

            listOf("""
                INSERT INTO countries VALUES ('1000', 'France');
                """.trimIndent(), """
                INSERT INTO artists VALUES ('2000', 'Claude Monet', '1000');
                """.trimIndent(), """
                INSERT INTO artists VALUES ('2001', 'Gustave Caillebotte', '1000');
                """.trimIndent(), """
                INSERT INTO mediums VALUES ('3000', 'Oil on canvas');
                """.trimIndent(), """
                INSERT INTO pictures VALUES ('4000', 'Haystacks', '1000', '2000', '3000');
                """.trimIndent(), """
                INSERT INTO pictures VALUES ('4001', 'Water Lilies', '1000', '2000', '3000');
                """.trimIndent()
            ).forEach(it::addBatch)
            it.executeBatch()
        }
    }
}