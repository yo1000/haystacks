package com.yo1000.haystacks.web.controller

import com.yo1000.haystacks.web.resource.DataSource
import com.yo1000.haystacks.web.resource.NoteFile
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.nio.file.Path

/**
 *
 * @author yo1000
 */
@RequestMapping("/api/info")
open class InformationRestController(
        private val dataSourceUrl: String,
        private val dataSourceName: String,
        private val dataSourceUsername: String,
        private val dataSourceDriverClassName: String,
        private val noteFilePath: Path
) {
    @GetMapping("/dataSource")
    fun get(): DataSource = DataSource(
            url = dataSourceUrl,
            name = dataSourceName,
            username = dataSourceUsername,
            driverClass = dataSourceDriverClassName
    )

    @GetMapping("/note")
    fun getFile(): NoteFile = NoteFile(
            path = noteFilePath.toAbsolutePath().toString(),
            name = noteFilePath.fileName.toString()
    )
}
