package com.yo1000.haystacks.autoconfigure.core

import com.yo1000.haystacks.core.repository.NoteRepository
import com.yo1000.haystacks.core.repository.PropertiesNoteRepository
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.nio.file.Paths

@Configuration
@EnableConfigurationProperties(NoteFileConfigurationProperties::class)
class PropertiesRepositoryAutoConfiguration(
        val props: NoteFileConfigurationProperties
) {
    @Bean
    fun noteRepository(): NoteRepository = PropertiesNoteRepository(
            props.storeLocation.takeIf {
                !it.trim().isEmpty()
            }?.let {
                Paths.get(it)
            } ?: Paths.get(System.getProperty("user.dir")).resolve("note.properties")
    )
}

@ConfigurationProperties("haystacks.note.file")
class NoteFileConfigurationProperties(
        var storeLocation: String = ""
)
